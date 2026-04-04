package com.livestream.livestream_api.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "live_streams")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LiveStream {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long streamId;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    @Builder.Default
    private int viewerCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private int likeCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StreamStatus status = StreamStatus.SCHEDULED;

    private String thumbnailUrl;

    @CreationTimestamp
    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Optimistic Locking for concurrent viewer/like updates
    @Version
    private Long version;



    public enum StreamStatus {
        SCHEDULED, LIVE, ENDED
    }


}