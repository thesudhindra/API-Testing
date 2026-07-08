package com.playground.enterprise.scheduler.dto;

import java.time.Instant;

public record ScheduledTaskResponse(
        String id,
        String tenantId,
        String taskType,
        String cronExpression,
        String payload,
        boolean enabled,
        Instant lastRunAt,
        Instant nextRunAt,
        Instant createdAt
) {
}
