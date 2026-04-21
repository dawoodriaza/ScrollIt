package com.livestream.livestream_api.controller;

import com.livestream.livestream_api.config.StreamConcurrencyManager;
import com.livestream.livestream_api.dto.request.GiftRequest;
import com.livestream.livestream_api.dto.response.ApiResponse;
import com.livestream.livestream_api.exception.ResourceNotFoundException;
import com.livestream.livestream_api.model.LiveStream;
import com.livestream.livestream_api.repository.LiveStreamRepository;
import com.livestream.livestream_api.repository.UserRepository;
import com.livestream.livestream_api.service.GiftService;
import com.livestream.livestream_api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository           userRepository;
    private final LiveStreamRepository     streamRepository;
    private final UserService              userService;
    private final GiftService              giftService;
    private final StreamConcurrencyManager concurrency;

    @GetMapping("/users")
    public ResponseEntity<Page<ApiResponse.UserSummary>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.getAllUsers(PageRequest.of(page, size)));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse.UserSummary> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/users/{id}/coins")
    public ResponseEntity<ApiResponse.UserSummary> addCoins(
            @PathVariable Long id,
            @RequestParam int amount,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.addCoins(id, amount, userDetails));
    }

    @DeleteMapping("/users/{id}/soft")
    public ResponseEntity<ApiResponse.MessageResponse> softDeleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.softDeleteUser(id, userDetails));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse.MessageResponse> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.deleteUser(id, userDetails));
    }

    @GetMapping("/streams")
    public ResponseEntity<Page<ApiResponse.StreamSummary>> getAllStreams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                streamRepository.findAll(PageRequest.of(page, size, Sort.by("startedAt").descending()))
                        .map(stream -> {
                            ApiResponse.StreamSummary s = ApiResponse.StreamSummary.from(stream);
                            s.setViewerCount(concurrency.getViewerCount(stream.getStreamId()));
                            s.setLikeCount(concurrency.getLikeCount(stream.getStreamId()));
                            return s;
                        })
        );
    }

    @PutMapping("/streams/{id}/force-end")
    public ResponseEntity<ApiResponse.MessageResponse> forceEndStream(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails adminDetails) {
        LiveStream stream = streamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stream", id));
        if (stream.getStatus() != LiveStream.StreamStatus.LIVE)
            return ResponseEntity.ok(new ApiResponse.MessageResponse("Stream is not live."));
        stream.setViewerCount(concurrency.getViewerCount(id));
        stream.setLikeCount(concurrency.getLikeCount(id));
        stream.setStatus(LiveStream.StreamStatus.ENDED);
        stream.setEndedAt(LocalDateTime.now());
        streamRepository.save(stream);
        concurrency.cleanupStream(id);
        log.warn("[ADMIN] {} force-ended stream: {}", adminDetails.getUsername(), stream.getTitle());
        return ResponseEntity.ok(new ApiResponse.MessageResponse("Stream force-ended by admin."));
    }

    @DeleteMapping("/streams/{id}")
    public ResponseEntity<ApiResponse.MessageResponse> forceDeleteStream(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails adminDetails) {
        LiveStream stream = streamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stream", id));
        streamRepository.delete(stream);
        concurrency.cleanupStream(id);
        log.warn("[ADMIN] {} deleted stream: {}", adminDetails.getUsername(), stream.getTitle());
        return ResponseEntity.ok(new ApiResponse.MessageResponse("Stream deleted by admin."));
    }

    @GetMapping("/gifts")
    public ResponseEntity<?> getAllGifts() {
        return ResponseEntity.ok(giftService.getAllGifts());
    }

    @PostMapping("/gifts")
    public ResponseEntity<ApiResponse.GiftSummary> createGift(@RequestBody GiftRequest.Create req) {
        return ResponseEntity.ok(giftService.createGift(req));
    }

    @PutMapping("/gifts/{id}")
    public ResponseEntity<ApiResponse.GiftSummary> updateGift(
            @PathVariable Long id, @RequestBody GiftRequest.Update req) {
        return ResponseEntity.ok(giftService.updateGift(id, req));
    }

    @DeleteMapping("/gifts/{id}")
    public ResponseEntity<ApiResponse.MessageResponse> deleteGift(@PathVariable Long id) {
        return ResponseEntity.ok(giftService.deleteGift(id));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers",  userRepository.count());
        stats.put("totalStreams", streamRepository.count());
        stats.put("liveStreams",  streamRepository.findByStatus(LiveStream.StreamStatus.LIVE).size());
        return ResponseEntity.ok(stats);
    }
}