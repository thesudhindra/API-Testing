package com.playground.playground.config.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpsertConfigRequest(
        @NotBlank @Size(max = 1024) String value
) {
}
