package com.playground.enterprise.aml.dto;

import com.playground.enterprise.domain.ScreeningType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AmlScreeningRequest(
        @NotBlank String partyId,
        @NotNull ScreeningType screeningType
) {
}
