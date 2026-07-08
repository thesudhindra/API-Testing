package com.playground.enterprise.aml.dto;

import com.playground.enterprise.domain.AmlCaseStatus;
import com.playground.enterprise.domain.AmlCaseType;
import com.playground.enterprise.domain.AmlPriority;

import java.time.Instant;

public record AmlCaseResponse(
        String id,
        String partyId,
        AmlCaseType caseType,
        AmlCaseStatus status,
        AmlPriority priority,
        String assignedTo,
        Instant createdAt,
        Instant updatedAt
) {
}
