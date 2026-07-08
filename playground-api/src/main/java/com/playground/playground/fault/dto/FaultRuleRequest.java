package com.playground.playground.fault.dto;

import com.playground.playground.domain.FaultTargetService;
import com.playground.playground.domain.FaultType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record FaultRuleRequest(
        @NotNull FaultTargetService targetService,
        @NotBlank @Size(max = 256) String pathPattern,
        @NotNull FaultType faultType,
        Map<String, Object> config
) {
}
