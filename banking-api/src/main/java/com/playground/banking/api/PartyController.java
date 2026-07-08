package com.playground.banking.api;

import com.playground.common.dto.PageResponse;
import com.playground.banking.customer.dto.CreatePartyRequest;
import com.playground.banking.customer.dto.PartyResponse;
import com.playground.banking.customer.dto.UpdatePartyRequest;
import com.playground.banking.customer.service.PartyService;
import com.playground.banking.domain.PartyStatus;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/parties")
@Tag(name = "Customers", description = "Party (customer) lifecycle")
@SecurityRequirement(name = "bearerAuth")
public class PartyController {

    private final PartyService partyService;

    public PartyController(PartyService partyService) {
        this.partyService = partyService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPS_AGENT', 'ADMIN')")
    @Operation(summary = "List parties")
    public PageResponse<PartyResponse> listParties(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestParam(required = false) PartyStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        return partyService.listParties(tenantId, status, page, size);
    }

    @GetMapping("/{partyId}")
    @Operation(summary = "Get party by id")
    public PartyResponse getParty(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String partyId) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePartyAccess(jwt, partyId);
        return partyService.getParty(tenantId, partyId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('OPS_AGENT', 'ADMIN')")
    @Operation(summary = "Create party")
    public PartyResponse createParty(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @Valid @RequestBody CreatePartyRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        return partyService.createParty(tenantId, request);
    }

    @PatchMapping("/{partyId}")
    @Operation(summary = "Update party")
    public PartyResponse updateParty(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String partyId,
            @Valid @RequestBody UpdatePartyRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        if (!TenantAccess.isPrivileged(jwt)) {
            TenantAccess.requirePartyAccess(jwt, partyId);
        }
        return partyService.updateParty(tenantId, partyId, request, TenantAccess.isPrivileged(jwt));
    }
}
