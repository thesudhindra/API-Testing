package com.playground.playground.failure.dto;

import com.playground.playground.domain.FaultTargetService;
import com.playground.playground.domain.FaultType;

import java.util.List;
import java.util.Map;

public record FailureSimulation(
        String slug,
        String title,
        String description,
        FaultTargetService targetService,
        FaultType faultType,
        String pathPattern,
        Map<String, Object> faultConfig,
        List<String> relatedMocks
) {
}
