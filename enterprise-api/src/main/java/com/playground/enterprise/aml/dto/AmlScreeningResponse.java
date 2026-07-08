package com.playground.enterprise.aml.dto;

import com.playground.enterprise.domain.ScreeningResult;
import com.playground.enterprise.domain.ScreeningType;

import java.time.Instant;

public record AmlScreeningResponse(
        String id,
        String partyId,
        ScreeningType screeningType,
        ScreeningResult result,
        Integer matchScore,
        String caseId,
        Instant createdAt
) {
}
