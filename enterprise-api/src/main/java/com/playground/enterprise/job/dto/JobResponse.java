package com.playground.enterprise.job.dto;

import com.playground.enterprise.domain.JobStatus;

import java.time.Instant;

public record JobResponse(
        String id,
        String tenantId,
        String jobType,
        String payload,
        JobStatus status,
        int progress,
        String result,
        String errorMessage,
        int retryCount,
        int maxRetries,
        Instant createdAt,
        Instant updatedAt
) {
}
