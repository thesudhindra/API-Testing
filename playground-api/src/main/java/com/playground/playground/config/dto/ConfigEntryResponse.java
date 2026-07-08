package com.playground.playground.config.dto;

import java.time.Instant;

public record ConfigEntryResponse(
        String key,
        String value,
        Instant updatedAt
) {
}
