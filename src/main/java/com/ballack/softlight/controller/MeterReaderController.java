package com.ballack.softlight.controller;

import com.ballack.softlight.dto.MeterReadingResponse;
import com.ballack.softlight.dto.SessionMeterResponse;
import com.ballack.softlight.service.MeterReaderService;
import com.ballack.softlight.service.SessionMeterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/meter")
@RequiredArgsConstructor
public class MeterReaderController {

    private final MeterReaderService service;
    private final SessionMeterService sessionMeterService;

    @PostMapping("/read")
    public SessionMeterResponse readMeter(
            @RequestParam("user_id") Long userId,
            @RequestParam("type") String type,
            @RequestParam("session") String session,
            @RequestParam("image") MultipartFile image
    ) throws Exception {

        return sessionMeterService.process(userId, image, type,session);
    }
    @PostMapping("/make")
    public MeterReadingResponse createMeter(
            @RequestParam("user_id") Long userId,
            @RequestParam("type") String type,
            @RequestParam("session") String session,
            @RequestParam("image") MultipartFile image
    ) throws Exception {

        return service.process(userId, image, type,session);
    }
}
