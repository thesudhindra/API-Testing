package com.playground.enterprise.document.dto;

import com.playground.enterprise.domain.DocumentStatus;

import java.time.Instant;

public record DocumentResponse(
        String id,
        String partyId,
        String documentType,
        String fileName,
        String contentType,
        String storageKey,
        DocumentStatus status,
        Instant createdAt
) {
}
