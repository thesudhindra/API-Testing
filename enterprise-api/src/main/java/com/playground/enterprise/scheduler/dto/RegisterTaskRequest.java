package com.playground.enterprise.scheduler.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterTaskRequest(
        @NotBlank String taskType,
        @NotBlank String cronExpression,
        String payload
) {
}
