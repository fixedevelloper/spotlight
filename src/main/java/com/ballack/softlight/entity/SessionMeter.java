package com.ballack.softlight.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Table(name = "session_meter")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionMeter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String valueNumber;

    private String type;

    @Column(columnDefinition = "TEXT")
    private String rawText;

    private String imagePath;
    private String sessionId;
    private LocalDateTime createdAt;

}
