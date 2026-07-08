package com.playground.enterprise.job.dto;

import jakarta.validation.constraints.NotBlank;

public record SubmitJobRequest(
        @NotBlank String jobType,
        String payload
) {
}
