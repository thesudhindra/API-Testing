package com.playground.playground.mock.dto;

public record MockResponse(
        int statusCode,
        String body,
        int delayMs
) {
}
