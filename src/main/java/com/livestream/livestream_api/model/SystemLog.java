package com.livestream.livestream_api.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "system_logs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogType logType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogLevel level;

    @Column(nullable = false, length = 1000)
    private String message;


    private Long userId;
    private String username;


    private Long streamId;


    @Column(length = 2000)
    private String detail;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum LogType {
        AUTH,
        STREAM,
        GIFT,
        LIKE,
        COMMENT,
        VIEWER,
        COIN,
        ERROR,
        ADMIN
    }

    public enum LogLevel {
        INFO, WARN, ERROR
    }
}