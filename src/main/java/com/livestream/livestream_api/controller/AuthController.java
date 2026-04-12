package com.livestream.livestream_api.controller;



import com.livestream.livestream_api.dto.request.AuthRequest;
import com.livestream.livestream_api.dto.response.ApiResponse;
import com.livestream.livestream_api.exception.BadRequestException;
import com.livestream.livestream_api.repository.UserRepository;
import com.livestream.livestream_api.service.AuthService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse.MessageResponse> register(@Valid @RequestBody AuthRequest.Register req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse.AuthToken> login(@Valid @RequestBody AuthRequest.Login req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse.MessageResponse> verifyEmail(@RequestParam String token) {
        return ResponseEntity.ok(authService.verifyEmail(token));
    }



    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse.MessageResponse> forgotPassword(@Valid @RequestBody AuthRequest.ForgotPassword req) {
        return ResponseEntity.ok(authService.forgotPassword(req));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse.MessageResponse> resetPassword(@Valid @RequestBody AuthRequest.ResetPassword req) {
        return ResponseEntity.ok(authService.resetPassword(req));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse.MessageResponse> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AuthRequest.ChangePassword req) {
        return ResponseEntity.ok(authService.changePassword(userDetails.getUsername(), req));
    }


}
