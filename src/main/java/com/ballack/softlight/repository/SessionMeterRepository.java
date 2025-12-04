package com.ballack.softlight.repository;

import com.ballack.softlight.entity.SessionMeter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionMeterRepository extends JpaRepository<SessionMeter, Long> {
    List<SessionMeter> findBySessionId(String sessionId);
}