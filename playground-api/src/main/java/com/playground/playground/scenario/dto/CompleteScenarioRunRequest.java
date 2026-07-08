package com.playground.playground.scenario.dto;

import com.playground.playground.domain.ScenarioRunStatus;
import jakarta.validation.constraints.NotNull;

public record CompleteScenarioRunRequest(
        @NotNull ScenarioRunStatus status
) {
}
