package com.ballack.softlight.service;

import com.ballack.softlight.dto.SessionMeterResponse;
import com.ballack.softlight.entity.SessionMeter;
import com.ballack.softlight.repository.SessionMeterRepository;
import com.ballack.softlight.util.OCRUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SessionMeterService {

    private final SessionMeterRepository repository;
    public SessionMeterResponse process(Long userId, MultipartFile image, String type, String session) throws Exception {

        String digits = OCRUtil.extractFromRealMeter(image);

        String meterNumber = null;
        String index = null;

        if (type.equals("number")) {
            meterNumber = digits;
        } else if (type.equals("index")) {
            index = digits;
        }


        SessionMeter saved = repository.save(
                SessionMeter.builder()
                        .userId(userId)
                        .valueNumber(meterNumber)
                        .type(type)
                        .rawText(digits)
                        .sessionId(session)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return SessionMeterResponse.builder()
                .id(saved.getId())
                .userId(saved.getUserId())
                .valueNumber(saved.getValueNumber())
                .type(saved.getType())
                .rawText(saved.getRawText())
                .sessionId(saved.getSessionId())
                .timestamp(saved.getCreatedAt())
                .build();
    }

    private String extractMeterNumber(String text) {
        // Numéro de compteur = généralement 6 à 12 digits
        return text.replaceAll(".*?(\\d{6,12}).*", "$1");
    }

    private String extractIndex(String text) {
        // Index = 3 à 7 digits
        return text.replaceAll(".*?(\\d{3,7}).*", "$1");
    }
}