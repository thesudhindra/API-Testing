package com.playground.platform.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DemoValidationRequest(
        @NotBlank(message = "name must not be blank")
        @Size(min = 2, max = 50, message = "name must be between 2 and 50 characters")
        String name
) {
}
