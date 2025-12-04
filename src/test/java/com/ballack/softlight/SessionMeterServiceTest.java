package com.ballack.softlight;


import com.ballack.softlight.dto.SessionMeterResponse;
import com.ballack.softlight.entity.SessionMeter;
import com.ballack.softlight.repository.SessionMeterRepository;
import com.ballack.softlight.service.SessionMeterService;
import com.ballack.softlight.util.OCRUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionMeterServiceTest {

    @Mock
    private SessionMeterRepository repository;

    @InjectMocks
    private SessionMeterService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcess_NumberType_Success() throws Exception {
        MockMultipartFile image = new MockMultipartFile("file", "meter.jpg", "image/jpeg", "dummy".getBytes());
        String extractedDigits = "123456";

        // Mock statique OCRUtil
        try (MockedStatic<OCRUtil> ocrMock = mockStatic(OCRUtil.class)) {
            ocrMock.when(() -> OCRUtil.extractFromRealMeter(any())).thenReturn(extractedDigits);

            SessionMeter savedMeter = SessionMeter.builder()
                    .id(1L)
                    .userId(42L)
                    .valueNumber(extractedDigits)
                    .type("number")
                    .rawText(extractedDigits)
                    .sessionId("session123")
                    .createdAt(LocalDateTime.now())
                    .build();

            when(repository.save(any(SessionMeter.class))).thenReturn(savedMeter);

            SessionMeterResponse response = service.process(42L, image, "number", "session123");

            // Assertions
            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals(42L, response.getUserId());
            assertEquals("123456", response.getValueNumber());
            assertEquals("number", response.getType());
            assertEquals("123456", response.getRawText());
            assertEquals("session123", response.getSessionId());

            // Vérifier l'objet passé à repository.save
            ArgumentCaptor<SessionMeter> captor = ArgumentCaptor.forClass(SessionMeter.class);
            verify(repository, times(1)).save(captor.capture());
            SessionMeter captured = captor.getValue();
            assertEquals("123456", captured.getValueNumber());
            assertEquals("number", captured.getType());
        }
    }

    @Test
    void testProcess_IndexType_Success() throws Exception {
        MockMultipartFile image = new MockMultipartFile("file", "meter.jpg", "image/jpeg", "dummy".getBytes());
        String extractedDigits = "6543";

        try (MockedStatic<OCRUtil> ocrMock = mockStatic(OCRUtil.class)) {
            ocrMock.when(() -> OCRUtil.extractFromRealMeter(any())).thenReturn(extractedDigits);

            SessionMeter savedMeter = SessionMeter.builder()
                    .id(2L)
                    .userId(42L)
                    .type("index")
                    .rawText(extractedDigits)
                    .sessionId("session456")
                    .build();

            when(repository.save(any(SessionMeter.class))).thenReturn(savedMeter);

            SessionMeterResponse response = service.process(42L, image, "index", "session456");
            System.out.println("Response: " + response);
            assertNotNull(response);
            assertEquals(2L, response.getId());
            assertEquals("index", response.getType());
            assertNull(response.getValueNumber());
            assertEquals("6543", response.getRawText());
        }
    }

    @Test
    void testProcess_InvalidType_ShouldNotFail() throws Exception {
        MockMultipartFile image = new MockMultipartFile("file", "meter.jpg", "image/jpeg", "dummy".getBytes());
        String extractedDigits = "999999";

        try (MockedStatic<OCRUtil> ocrMock = mockStatic(OCRUtil.class)) {
            ocrMock.when(() -> OCRUtil.extractFromRealMeter(any())).thenReturn(extractedDigits);

            SessionMeter savedMeter = SessionMeter.builder()
                    .id(3L)
                    .userId(42L)
                    .type("unknown")
                    .rawText(extractedDigits)
                    .sessionId("session789")
                    .build();

            when(repository.save(any(SessionMeter.class))).thenReturn(savedMeter);

            SessionMeterResponse response = service.process(42L, image, "unknown", "session789");

            assertNotNull(response);
            assertEquals("unknown", response.getType());
            assertNull(response.getValueNumber());
            assertEquals("999999", response.getRawText());
        }
    }
}
