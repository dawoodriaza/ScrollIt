package com.livestream.livestream_api.controller;

import com.livestream.livestream_api.dto.request.CommentRequest;
import com.livestream.livestream_api.dto.request.GiftRequest;
import com.livestream.livestream_api.dto.response.ApiResponse;
import com.livestream.livestream_api.service.StreamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/streams/{streamId}")
@RequiredArgsConstructor
public class StreamInteractionController {

    private final StreamService streamService;

    @PostMapping("/likes")
    public ResponseEntity<ApiResponse.MessageResponse> toggleLike(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long streamId) {
        return ResponseEntity.ok(streamService.toggleLike(userDetails.getUsername(), streamId));
    }

    @GetMapping("/likes/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long streamId) {
        return ResponseEntity.ok(streamService.getLikeCount(streamId));
    }

    @PostMapping("/comments")
    public ResponseEntity<ApiResponse.CommentSummary> postComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long streamId,
            @Valid @RequestBody CommentRequest.Create req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(streamService.postComment(userDetails.getUsername(), streamId, req));
    }

    @GetMapping("/comments")
    public ResponseEntity<Page<ApiResponse.CommentSummary>> getComments(
            @PathVariable Long streamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(streamService.getStreamComments(streamId,
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse.CommentSummary> updateComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long streamId, @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest.Update req) {
        return ResponseEntity.ok(streamService.updateComment(userDetails.getUsername(), commentId, req));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse.MessageResponse> deleteComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long streamId, @PathVariable Long commentId) {
        return ResponseEntity.ok(streamService.deleteComment(userDetails.getUsername(), commentId));
    }

    @PostMapping("/gifts")
    public ResponseEntity<ApiResponse.TransactionSummary> sendGift(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long streamId,
            @Valid @RequestBody GiftRequest.Send req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(streamService.sendGift(userDetails.getUsername(), streamId, req));
    }

    @GetMapping("/gifts")
    public ResponseEntity<Page<ApiResponse.TransactionSummary>> getStreamGifts(
            @PathVariable Long streamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(streamService.getStreamTransactions(streamId, PageRequest.of(page, size)));
    }
}