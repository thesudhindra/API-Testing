package com.playground.banking.identity.dto;

public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
