package com.playground.playground.reset.dto;

import com.playground.playground.domain.ResetScope;
import jakarta.validation.constraints.NotNull;

public record ResetRequest(
        @NotNull ResetScope scope
) {
}
