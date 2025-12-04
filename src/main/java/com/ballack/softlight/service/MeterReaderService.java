package com.ballack.softlight.service;

import com.ballack.softlight.dto.MeterReadingResponse;
import com.ballack.softlight.entity.MeterReading;
import com.ballack.softlight.repository.MeterReadingRepository;
import com.ballack.softlight.util.OCRUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MeterReaderService {

    private final MeterReadingRepository repository;
    public MeterReadingResponse process(Long userId, MultipartFile image, String type,String session) throws Exception {

        String digits = OCRUtil.extractFromRealMeter(image);

        String meterNumber = null;
        String index = null;

        if (type.equals("number")) {
            meterNumber = digits;
        } else if (type.equals("index")) {
            index = digits;
        }


        MeterReading saved = repository.save(
                MeterReading.builder()
                        .userId(userId)
                        .meterNumber(meterNumber)
                        .consumptionIndex(index)
                        .rawText(digits)
                        .sessionId(session)
                        .build()
        );

        return MeterReadingResponse.builder()
                .id(saved.getId())
                .userId(saved.getUserId())
                .meterNumber(saved.getMeterNumber())
                .consumptionIndex(saved.getConsumptionIndex())
                .rawText(saved.getRawText())
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