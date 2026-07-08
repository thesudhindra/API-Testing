package com.playground.playground.fault.dto;

import com.playground.playground.domain.FaultTargetService;
import com.playground.playground.domain.FaultType;

import java.time.Instant;
import java.util.Map;

public record FaultRuleResponse(
        String id,
        FaultTargetService targetService,
        String pathPattern,
        FaultType faultType,
        Map<String, Object> config,
        boolean enabled,
        Instant expiresAt,
        Instant createdAt
) {
}
