package com.playground.enterprise.api;

import com.playground.enterprise.deposit.fixed.dto.CreateFixedDepositRequest;
import com.playground.enterprise.deposit.fixed.dto.FixedDepositResponse;
import com.playground.enterprise.deposit.fixed.service.FixedDepositService;
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
@RequestMapping("/v1/fixed-deposits")
@Tag(name = "Fixed Deposits", description = "Fixed deposit accounts")
@SecurityRequirement(name = "bearerAuth")
public class FixedDepositController {

    private final FixedDepositService fixedDepositService;

    public FixedDepositController(FixedDepositService fixedDepositService) {
        this.fixedDepositService = fixedDepositService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create fixed deposit")
    public FixedDepositResponse create(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @Valid @RequestBody CreateFixedDepositRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePartyAccess(jwt, request.partyId());
        return fixedDepositService.create(tenantId, jwt.getSubject(), request);
    }

    @GetMapping("/{depositId}")
    @Operation(summary = "Get fixed deposit by id")
    public FixedDepositResponse get(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String depositId) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        FixedDepositResponse deposit = fixedDepositService.get(tenantId, depositId);
        TenantAccess.requirePartyAccess(jwt, deposit.partyId());
        return deposit;
    }

    @GetMapping
    @Operation(summary = "List fixed deposits for a party")
    public PageResponse<FixedDepositResponse> listByParty(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestParam String partyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePartyAccess(jwt, partyId);
        return fixedDepositService.listByParty(tenantId, partyId, page, size);
    }
}
