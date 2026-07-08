package com.playground.playground.reset.dto;

import com.playground.playground.domain.ResetScope;

import java.time.Instant;

public record ResetResponse(
        String id,
        ResetScope scope,
        String status,
        String details,
        Instant createdAt
) {
}
