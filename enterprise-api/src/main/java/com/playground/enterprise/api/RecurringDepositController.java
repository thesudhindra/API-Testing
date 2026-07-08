package com.playground.enterprise.api;

import com.playground.enterprise.deposit.recurring.dto.CreateRecurringDepositRequest;
import com.playground.enterprise.deposit.recurring.dto.InstallmentResponse;
import com.playground.enterprise.deposit.recurring.dto.RecurringDepositResponse;
import com.playground.enterprise.deposit.recurring.service.RecurringDepositService;
import com.playground.common.security.TenantAccess;
import com.playground.common.dto.PageResponse;
import com.playground.common.api.ApiHeaders;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/recurring-deposits")
@Tag(name = "Recurring Deposits", description = "Recurring deposit accounts and installments")
@SecurityRequirement(name = "bearerAuth")
public class RecurringDepositController {

    private final RecurringDepositService recurringDepositService;

    public RecurringDepositController(RecurringDepositService recurringDepositService) {
        this.recurringDepositService = recurringDepositService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create recurring deposit")
    public RecurringDepositResponse create(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @Valid @RequestBody CreateRecurringDepositRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePartyAccess(jwt, request.partyId());
        return recurringDepositService.create(tenantId, jwt.getSubject(), request);
    }

    @GetMapping("/{depositId}")
    @Operation(summary = "Get recurring deposit by id")
    public RecurringDepositResponse get(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String depositId) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        RecurringDepositResponse deposit = recurringDepositService.get(tenantId, depositId);
        TenantAccess.requirePartyAccess(jwt, deposit.partyId());
        return deposit;
    }

    @GetMapping
    @Operation(summary = "List recurring deposits for a party")
    public PageResponse<RecurringDepositResponse> listByParty(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestParam String partyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePartyAccess(jwt, partyId);
        return recurringDepositService.listByParty(tenantId, partyId, page, size);
    }

    @PostMapping("/{depositId}/installments")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Record installment payment")
    public InstallmentResponse recordInstallment(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String depositId) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        RecurringDepositResponse deposit = recurringDepositService.get(tenantId, depositId);
        TenantAccess.requirePartyAccess(jwt, deposit.partyId());
        return recurringDepositService.recordInstallment(tenantId, jwt.getSubject(), depositId);
    }
}
