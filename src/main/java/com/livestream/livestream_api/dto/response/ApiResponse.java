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
    public static class StreamSummary {
        private Long streamId;
        private String title;
        private String description;
        private int viewerCount;
        private int likeCount;
        private String status;
        private String thumbnailUrl;
        private Long hostId;
        private String hostUsername;
        private LocalDateTime startedAt;
        private LocalDateTime endedAt;

        public static StreamSummary from(LiveStream s) {
            return StreamSummary.builder()
                    .streamId(s.getStreamId())
                    .title(s.getTitle())
                    .description(s.getDescription())
                    .viewerCount(s.getViewerCount())
                    .likeCount(s.getLikeCount())
                    .status(s.getStatus().name())
                    .thumbnailUrl(s.getThumbnailUrl())
                    .hostId(s.getHost().getUserId())
                    .hostUsername(s.getHost().getUsername())
                    .startedAt(s.getStartedAt())
                    .endedAt(s.getEndedAt())
                    .build();
        }
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CommentSummary {
        private Long commentId;
        private String message;
        private Long userId;
        private String username;
        private Long streamId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static CommentSummary from(Comment c) {
            return CommentSummary.builder()
                    .commentId(c.getCommentId())
                    .message(c.getMessage())
                    .userId(c.getUser().getUserId())
                    .username(c.getUser().getUsername())
                    .streamId(c.getStream().getStreamId())
                    .createdAt(c.getCreatedAt())
                    .updatedAt(c.getUpdatedAt())
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
    public static class TransactionSummary {
        private Long transactionId;
        private Long senderId;
        private String senderUsername;
        private Long streamId;
        private Long giftId;
        private String giftName;
        private int coinsSpent;
        private LocalDateTime createdAt;

        public static TransactionSummary from(GiftTransaction t) {
            return TransactionSummary.builder()
                    .transactionId(t.getTransactionId())
                    .senderId(t.getSender().getUserId())
                    .senderUsername(t.getSender().getUsername())
                    .streamId(t.getStream().getStreamId())
                    .giftId(t.getGift().getGiftId())
                    .giftName(t.getGift().getGiftName())
                    .coinsSpent(t.getCoinsSpent())
                    .createdAt(t.getCreatedAt())
                    .build();
        }
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ViewerSummary {
        private Long viewerId;
        private Long userId;
        private String username;
        private String guestName;
        private String displayName;
        private Long streamId;
        private LocalDateTime joinTime;
        private LocalDateTime leaveTime;
        private String status;

        public static ViewerSummary from(StreamViewer sv) {
            return ViewerSummary.builder()
                    .viewerId(sv.getViewerId())
                    .userId(sv.getUser() != null ? sv.getUser().getUserId() : null)
                    .username(sv.getUser() != null ? sv.getUser().getUsername() : null)
                    .guestName(sv.getGuestName())
                    .displayName(sv.getDisplayName())
                    .streamId(sv.getStream().getStreamId())
                    .joinTime(sv.getJoinTime())
                    .leaveTime(sv.getLeaveTime())
                    .status(sv.getStatus().name())
                    .build();
        }
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class MessageResponse {
        private String message;
    }
}