package com.ballack.softlight.repository;

import com.ballack.softlight.entity.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {
    List<MeterReading> findBySessionId(String sessionId);
}