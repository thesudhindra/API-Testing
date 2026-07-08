package com.playground.enterprise.report.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GenerateReportRequest(
        @NotBlank @Size(max = 64) String reportType,
        @Size(max = 1024) String parameters
) {
}
