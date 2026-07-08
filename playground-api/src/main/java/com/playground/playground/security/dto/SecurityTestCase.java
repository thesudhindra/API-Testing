package com.playground.playground.security.dto;

public record SecurityTestCase(
        String id,
        String category,
        String title,
        String description,
        String targetService,
        String method,
        String targetPath,
        int expectedStatus
) {
}
