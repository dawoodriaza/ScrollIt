package com.livestream.livestream_api.controller;

import com.livestream.livestream_api.dto.request.LiveStreamRequest;
import com.livestream.livestream_api.dto.response.ApiResponse;
import com.livestream.livestream_api.service.LiveStreamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/streams")
@RequiredArgsConstructor
public class LiveStreamController {

    private final LiveStreamService streamService;

    @GetMapping
    public ResponseEntity<Page<ApiResponse.StreamSummary>> getLiveStreams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(streamService.getAllLiveStreams(PageRequest.of(page, size, Sort.by("startedAt").descending())));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ApiResponse.StreamSummary>> searchStreams(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(streamService.searchStreams(keyword, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse.StreamSummary> getStream(@PathVariable Long id) {
        return ResponseEntity.ok(streamService.getStreamById(id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ApiResponse.StreamSummary>> getMyStreams(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(streamService.getMyStreams(userDetails.getUsername()));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse.StreamSummary> createStream(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestPart("data") LiveStreamRequest.Create req,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(streamService.createStream(userDetails.getUsername(), req, thumbnail));
    }
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse.StreamSummary> createStreamJson(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody LiveStreamRequest.Create req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(streamService.createStream(userDetails.getUsername(), req, null));
    }


    @PostMapping(value = "/{id}/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse.StreamSummary> uploadThumbnail(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(streamService.uploadThumbnail(userDetails.getUsername(), id, file));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse.StreamSummary> updateStream(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody LiveStreamRequest.Update req) {
        return ResponseEntity.ok(streamService.updateStream(userDetails.getUsername(), id, req));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<ApiResponse.StreamSummary> startStream(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        return ResponseEntity.ok(streamService.startStream(userDetails.getUsername(), id));
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<ApiResponse.StreamSummary> endStream(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        return ResponseEntity.ok(streamService.endStream(userDetails.getUsername(), id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse.MessageResponse> deleteStream(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        return ResponseEntity.ok(streamService.deleteStream(userDetails.getUsername(), id));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<ApiResponse.ViewerSummary> joinStream(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(streamService.joinStream(userDetails.getUsername(), id));
    }


    @PostMapping("/{id}/join/guest")
    public ResponseEntity<ApiResponse.ViewerSummary> joinStreamAsGuest(
            @PathVariable Long id,
            @RequestParam(required = false) String guestName) {
        return ResponseEntity.ok(streamService.joinStreamAsGuest(id, guestName));
    }


    @PostMapping("/{id}/leave")
    public ResponseEntity<ApiResponse.MessageResponse> leaveStream(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(streamService.leaveStream(userDetails.getUsername(), id));
    }


    @PostMapping("/{id}/leave/guest")
    public ResponseEntity<ApiResponse.MessageResponse> leaveStreamAsGuest(
            @PathVariable Long id,
            @RequestParam Long viewerId) {
        return ResponseEntity.ok(streamService.leaveStreamAsGuest(id, viewerId));
    }

    @GetMapping("/{id}/viewers")
    public ResponseEntity<List<ApiResponse.ViewerSummary>> getViewers(@PathVariable Long id) {
        return ResponseEntity.ok(streamService.getStreamViewers(id));
    }
}