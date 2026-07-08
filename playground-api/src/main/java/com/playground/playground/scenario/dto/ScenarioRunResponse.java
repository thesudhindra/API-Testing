package com.playground.playground.scenario.dto;

import com.playground.playground.domain.ScenarioRunStatus;

import java.time.Instant;

public record ScenarioRunResponse(
        String id,
        String scenarioSlug,
        ScenarioRunStatus status,
        Instant startedAt,
        Instant completedAt
) {
}
