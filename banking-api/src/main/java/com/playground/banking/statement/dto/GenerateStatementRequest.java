package com.playground.banking.statement.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GenerateStatementRequest(
        @NotNull LocalDate periodStart,
        @NotNull LocalDate periodEnd
) {
}
