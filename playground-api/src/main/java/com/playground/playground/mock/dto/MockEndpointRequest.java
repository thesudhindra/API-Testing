package com.playground.playground.mock.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MockEndpointRequest(
        @NotBlank @Size(max = 256) String path,
        @NotBlank @Size(max = 16) String httpMethod,
        @NotNull @Min(100) @Max(599) Integer statusCode,
        @NotBlank @Size(max = 4096) String responseBody,
        @Min(0) Integer delayMs,
        Boolean enabled
) {
}
