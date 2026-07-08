package com.playground.playground.seed.dto;

import jakarta.validation.constraints.NotBlank;

public record SeedGenerateRequest(
        @NotBlank String profile
) {
}
