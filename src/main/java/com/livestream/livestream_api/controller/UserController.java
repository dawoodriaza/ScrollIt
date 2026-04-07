package com.livestream.livestream_api.controller;



import com.livestream.livestream_api.dto.request.UserUpdateRequest;
import com.livestream.livestream_api.dto.response.ApiResponse;
import com.livestream.livestream_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse.UserSummary> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getMyProfile(userDetails.getUsername()));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse.UserSummary> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserUpdateRequest req) {
        return ResponseEntity.ok(userService.updateMyProfile(userDetails.getUsername(), req));
    }

}