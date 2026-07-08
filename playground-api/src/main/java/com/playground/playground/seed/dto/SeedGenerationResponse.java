package com.playground.playground.seed.dto;

import java.util.Map;

public record SeedGenerationResponse(
        String profile,
        Map<String, String> entities
) {
}
