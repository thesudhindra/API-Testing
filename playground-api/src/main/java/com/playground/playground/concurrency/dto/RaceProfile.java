package com.playground.playground.concurrency.dto;

import java.util.Map;

public record RaceProfile(
        String slug,
        String title,
        String targetService,
        String method,
        String targetPath,
        int concurrency,
        Map<String, Object> payloadTemplate,
        String notes
) {
}
