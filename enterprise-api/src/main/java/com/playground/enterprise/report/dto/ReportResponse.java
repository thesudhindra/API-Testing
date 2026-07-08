package com.playground.enterprise.report.dto;

import com.playground.enterprise.domain.ReportStatus;

import java.time.Instant;

public record ReportResponse(
        String id,
        String reportType,
        String parameters,
        ReportStatus status,
        String resultLocation,
        String requestedBy,
        Instant createdAt,
        Instant completedAt
) {
}
