package com.ballack.softlight.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SessionMeterResponse {

    private Long id;
    private Long userId;
    private String valueNumber;
    private String type;
    private String sessionId;
    private String rawText;
    private LocalDateTime timestamp;
}
