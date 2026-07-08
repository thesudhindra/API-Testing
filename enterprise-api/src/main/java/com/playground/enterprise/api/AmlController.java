package com.playground.enterprise.api;

import com.playground.enterprise.aml.dto.AmlCaseResponse;
import com.playground.enterprise.aml.dto.AmlScreeningRequest;
import com.playground.enterprise.aml.dto.AmlScreeningResponse;
import com.playground.enterprise.aml.dto.CreateAmlCaseRequest;
import com.playground.enterprise.aml.service.AmlService;
import com.playground.enterprise.domain.AmlCaseStatus;
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
@RequestMapping("/v1/aml")
@Tag(name = "AML", description = "Anti-money laundering cases and screenings")
@SecurityRequirement(name = "bearerAuth")
public class AmlController {

    private final AmlService amlService;

    public AmlController(AmlService amlService) {
        this.amlService = amlService;
    }

    @PostMapping("/cases")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create AML case")
    public AmlCaseResponse createCase(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @Valid @RequestBody CreateAmlCaseRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePrivileged(jwt);
        return amlService.createCase(tenantId, jwt.getSubject(), request);
    }

    @GetMapping("/cases/{caseId}")
    @Operation(summary = "Get AML case by id")
    public AmlCaseResponse getCase(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String caseId) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        AmlCaseResponse amlCase = amlService.getCase(tenantId, caseId);
        TenantAccess.requirePartyAccess(jwt, amlCase.partyId());
        return amlCase;
    }

    @GetMapping("/cases")
    @Operation(summary = "List AML cases for a party")
    public PageResponse<AmlCaseResponse> listCases(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestParam String partyId,
            @RequestParam(required = false) AmlCaseStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePartyAccess(jwt, partyId);
        return amlService.listCases(tenantId, partyId, status, page, size);
    }

    @PostMapping("/screenings")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Run AML screening")
    public AmlScreeningResponse runScreening(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @Valid @RequestBody AmlScreeningRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePartyAccess(jwt, request.partyId());
        return amlService.runScreening(tenantId, jwt.getSubject(), request);
    }
}
