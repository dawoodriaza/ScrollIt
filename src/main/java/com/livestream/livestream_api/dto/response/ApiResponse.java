package com.livestream.livestream_api.dto.response;

import com.livestream.livestream_api.model.*;
import lombok.*;

import java.time.LocalDateTime;

public class ApiResponse {



    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UserSummary {
        private Long userId;
        private String username;
        private String email;
        private int coinBalance;
        private String role;
        private String userStatus;
        private String profilePicture;
        private boolean emailVerified;
        private LocalDateTime createdAt;

        public static UserSummary from(User u) {
            return UserSummary.builder()
                    .userId(u.getUserId())
                    .username(u.getUsername())
                    .email(u.getEmail())
                    .coinBalance(u.getCoinBalance())
                    .role(u.getRole().name())
                    .userStatus(u.getUserStatus().name())
                    .profilePicture(u.getProfilePicture())
                    .emailVerified(u.isEmailVerified())
                    .createdAt(u.getCreatedAt())
                    .build();
        }
    }


}