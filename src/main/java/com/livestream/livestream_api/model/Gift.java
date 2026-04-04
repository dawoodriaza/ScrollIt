package com.livestream.livestream_api.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gifts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Gift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long giftId;

    @Column(nullable = false, unique = true)
    private String giftName;

    @Column(nullable = false)
    private int coinValue;

    private String iconUrl;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


}
