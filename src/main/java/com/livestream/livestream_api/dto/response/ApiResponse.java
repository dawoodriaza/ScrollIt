package com.livestream.livestream_api.dto.response;

import com.livestream.livestream_api.model.*;
import lombok.*;

import java.time.LocalDateTime;

public class ApiResponse {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AuthToken {
        private String accessToken;
        private String tokenType;
        private UserSummary user;
    }

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
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class GiftSummary {
        private Long giftId;
        private String giftName;
        private int coinValue;
        private String iconUrl;
        private boolean active;

        public static GiftSummary from(Gift g) {
            return GiftSummary.builder()
                    .giftId(g.getGiftId())
                    .giftName(g.getGiftName())
                    .coinValue(g.getCoinValue())
                    .iconUrl(g.getIconUrl())
                    .active(g.isActive())
                    .build();
        }
    }


    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class MessageResponse {
        private String message;
    }
}
