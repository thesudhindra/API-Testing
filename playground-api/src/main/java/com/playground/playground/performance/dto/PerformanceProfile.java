package com.playground.playground.performance.dto;

public record PerformanceProfile(
        String name,
        String targetPath,
        String method,
        int suggestedVUs,
        int durationSec,
        String notes
) {
}
