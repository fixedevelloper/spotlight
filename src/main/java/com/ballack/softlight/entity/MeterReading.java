package com.ballack.softlight.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "meter_readings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String meterNumber;

    private String consumptionIndex;

    @Column(columnDefinition = "TEXT")
    private String rawText;

    private String imagePath;
    private String sessionId;
    private LocalDateTime createdAt;
}
