package com.playground.playground.mock.dto;

import java.time.Instant;

public record MockEndpointResponse(
        String id,
        String path,
        String httpMethod,
        int statusCode,
        String responseBody,
        int delayMs,
        boolean enabled,
        Instant createdAt
) {
}
