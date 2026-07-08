package com.playground.banking.api;

import com.playground.banking.beneficiary.dto.BeneficiaryResponse;
import com.playground.banking.beneficiary.dto.CreateBeneficiaryRequest;
import com.playground.banking.beneficiary.service.BeneficiaryService;
import com.playground.common.security.TenantAccess;
import com.playground.common.api.ApiHeaders;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/v1/beneficiaries")
@Tag(name = "Beneficiaries", description = "Saved payee management")
@SecurityRequirement(name = "bearerAuth")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    @GetMapping
    @Operation(summary = "List beneficiaries for a party")
    public List<BeneficiaryResponse> listBeneficiaries(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestParam String partyId) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePartyAccess(jwt, partyId);
        return beneficiaryService.listByParty(tenantId, partyId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add beneficiary")
    public BeneficiaryResponse createBeneficiary(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @Valid @RequestBody CreateBeneficiaryRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePartyAccess(jwt, request.partyId());
        return beneficiaryService.create(tenantId, request);
    }

    @DeleteMapping("/{beneficiaryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove beneficiary (soft delete)")
    public void deleteBeneficiary(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String beneficiaryId) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        BeneficiaryResponse beneficiary = beneficiaryService.get(tenantId, beneficiaryId);
        TenantAccess.requirePartyAccess(jwt, beneficiary.partyId());
        beneficiaryService.delete(tenantId, beneficiaryId);
    }
}
