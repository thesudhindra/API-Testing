package com.playground.enterprise.resilience.dto;

import com.playground.enterprise.domain.CircuitBreakerState;

import java.time.Instant;

public record CircuitBreakerResponse(
        String name,
        CircuitBreakerState state,
        int failureCount,
        int successCount,
        Instant openedAt,
        Instant updatedAt
) {
}
