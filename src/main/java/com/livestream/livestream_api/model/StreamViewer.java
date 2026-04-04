package com.livestream.livestream_api.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "stream_viewers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StreamViewer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long viewerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stream_id", nullable = false)
    private LiveStream stream;

    @CreationTimestamp
    private LocalDateTime joinTime;

    private LocalDateTime leaveTime;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ViewerStatus status = ViewerStatus.WATCHING;

    public enum ViewerStatus {
        WATCHING, LEFT
    }
}