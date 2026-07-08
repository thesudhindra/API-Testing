package com.playground.playground.testdata.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TestDataGenerateRequest(
        @NotBlank @Size(max = 64) String namespace,
        @NotBlank String profile
) {
}
