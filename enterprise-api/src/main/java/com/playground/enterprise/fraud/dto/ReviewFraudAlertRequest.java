package com.playground.enterprise.fraud.dto;

import com.playground.enterprise.domain.FraudAlertStatus;
import jakarta.validation.constraints.NotNull;

public record ReviewFraudAlertRequest(
        @NotNull FraudAlertStatus status
) {
}
