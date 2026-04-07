package com.livestream.livestream_api.service;



import com.livestream.livestream_api.config.JwtUtil;
import com.livestream.livestream_api.dto.request.AuthRequest;
import com.livestream.livestream_api.dto.response.ApiResponse;
import com.livestream.livestream_api.exception.*;
import com.livestream.livestream_api.model.User;
import com.livestream.livestream_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final JwtUtil               jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService    userDetailsService;



    @Transactional
    public ApiResponse.MessageResponse register(AuthRequest.Register req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new DuplicateResourceException("Email already in use: " + req.getEmail());
        if (userRepository.existsByUsername(req.getUsername()))
            throw new DuplicateResourceException("Username already taken: " + req.getUsername());

        String verificationToken = UUID.randomUUID().toString();
        User user = User.builder()
                .username(req.getUsername()).email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(User.Role.USER).userStatus(User.UserStatus.ACTIVE)
                .emailVerified(false)
                .emailVerificationToken(verificationToken)
                .emailVerificationTokenExpiry(LocalDateTime.now().plusHours(24))
                .coinBalance(100).build();

        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);

        return new ApiResponse.MessageResponse("Registration successful! Please check your email to verify your account.");
    }





    @Transactional
    public ApiResponse.AuthToken login(AuthRequest.Login req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!user.isEmailVerified())
            throw new UnauthorizedException("Please verify your email before logging in.");
        if (user.getUserStatus() == User.UserStatus.INACTIVE)
            throw new UnauthorizedException("Your account has been deactivated.");

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(req.getEmail());
        String token = jwtUtil.generateToken(userDetails, user.getRole().name());


        return ApiResponse.AuthToken.builder()
                .accessToken(token).tokenType("Bearer")
                .user(ApiResponse.UserSummary.from(user)).build();
    }

}
