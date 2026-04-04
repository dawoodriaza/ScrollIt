package com.livestream.livestream_api.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
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


    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_user_id", nullable = false)
    private User host;

    @OneToMany(mappedBy = "stream", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<StreamViewer> viewers = new ArrayList<>();

    @OneToMany(mappedBy = "stream", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "stream", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "stream", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<GiftTransaction> giftTransactions = new ArrayList<>();


    public enum StreamStatus {
        SCHEDULED, LIVE, ENDED
    }


}