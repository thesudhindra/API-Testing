package com.playground.playground.testdata.dto;

import com.playground.playground.domain.TestDataEntityType;

import java.time.Instant;

public record TestDataHandleResponse(
        String id,
        String namespace,
        TestDataEntityType entityType,
        String entityId,
        String metadata,
        Instant createdAt
) {
}
