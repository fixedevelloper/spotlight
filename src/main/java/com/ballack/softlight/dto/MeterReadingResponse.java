package com.ballack.softlight.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeterReadingResponse {

    private Long id;
    private Long userId;
    private String meterNumber;
    private String consumptionIndex;
    private String rawText;
}
