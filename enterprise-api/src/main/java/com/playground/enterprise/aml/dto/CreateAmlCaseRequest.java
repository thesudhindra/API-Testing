package com.playground.enterprise.aml.dto;

import com.playground.enterprise.domain.AmlCaseType;
import com.playground.enterprise.domain.AmlPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAmlCaseRequest(
        @NotBlank String partyId,
        @NotNull AmlCaseType caseType,
        @NotNull AmlPriority priority
) {
}
