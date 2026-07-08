package com.playground.playground.performance.dto;

import java.util.List;
import java.util.Map;

public record LoadHintsResponse(
        List<PerformanceProfile> profiles,
        Map<String, String> hints
) {
}
