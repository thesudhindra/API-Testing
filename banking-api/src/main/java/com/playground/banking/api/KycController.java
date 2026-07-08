package com.playground.banking.api;

import com.playground.banking.kyc.dto.KycCaseResponse;
import com.playground.banking.kyc.dto.ReviewKycRequest;
import com.playground.banking.kyc.dto.SubmitKycRequest;
import com.playground.banking.kyc.service.KycService;
import com.playground.common.security.TenantAccess;
import com.playground.common.api.ApiHeaders;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

import java.util.List;

@RestController
@RequestMapping("/v1/kyc-cases")
@Tag(name = "KYC", description = "Know-your-customer case management")
@SecurityRequirement(name = "bearerAuth")
public class KycController {

    private final KycService kycService;

    public KycController(KycService kycService) {
        this.kycService = kycService;
    }

    @GetMapping
    @Operation(summary = "List KYC cases for a party")
    public List<KycCaseResponse> listCases(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestParam String partyId) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePartyAccess(jwt, partyId);
        return kycService.listByParty(tenantId, partyId);
    }

    @GetMapping("/{caseId}")
    @Operation(summary = "Get KYC case by id")
    public KycCaseResponse getCase(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String caseId) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        KycCaseResponse kycCase = kycService.getCase(tenantId, caseId);
        TenantAccess.requirePartyAccess(jwt, kycCase.partyId());
        return kycCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Submit KYC case")
    public KycCaseResponse submit(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @Valid @RequestBody SubmitKycRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePartyAccess(jwt, request.partyId());
        return kycService.submit(tenantId, request);
    }

    @PostMapping("/{caseId}/review")
    @PreAuthorize("hasAnyRole('OPS_AGENT', 'ADMIN')")
    @Operation(summary = "Review KYC case")
    public KycCaseResponse review(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String caseId,
            @Valid @RequestBody ReviewKycRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        return kycService.review(tenantId, caseId, request);
    }
}
