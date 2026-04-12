package com.livestream.livestream_api.controller;

import com.livestream.livestream_api.dto.request.GiftRequest;
import com.livestream.livestream_api.dto.response.ApiResponse;
import com.livestream.livestream_api.service.GiftService;
import com.livestream.livestream_api.service.StreamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/gifts")
@RequiredArgsConstructor
public class GiftController {

    private final GiftService   giftService;
    private final StreamService streamService;

    @GetMapping
    public ResponseEntity<List<ApiResponse.GiftSummary>> getActiveGifts() {
        return ResponseEntity.ok(giftService.getAllActiveGifts());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ApiResponse.GiftSummary>> getAllGifts() {
        return ResponseEntity.ok(giftService.getAllGifts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse.GiftSummary> getGift(@PathVariable Long id) {
        return ResponseEntity.ok(giftService.getGiftById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse.GiftSummary> createGift(@Valid @RequestBody GiftRequest.Create req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(giftService.createGift(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse.GiftSummary> updateGift(@PathVariable Long id, @Valid @RequestBody GiftRequest.Update req) {
        return ResponseEntity.ok(giftService.updateGift(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse.MessageResponse> deleteGift(@PathVariable Long id) {
        return ResponseEntity.ok(giftService.deleteGift(id));
    }

    @GetMapping("/my-transactions")
    public ResponseEntity<Page<ApiResponse.TransactionSummary>> getMyTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(streamService.getMyTransactions(userDetails.getUsername(), PageRequest.of(page, size)));
    }
}