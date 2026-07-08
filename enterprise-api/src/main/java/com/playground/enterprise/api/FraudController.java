package com.playground.enterprise.api;

import com.playground.enterprise.domain.FraudAlertStatus;
import com.playground.enterprise.fraud.dto.FraudAlertResponse;
import com.playground.enterprise.fraud.dto.FraudScreenRequest;
import com.playground.enterprise.fraud.dto.ReviewFraudAlertRequest;
import com.playground.enterprise.fraud.service.FraudService;
import com.playground.common.security.TenantAccess;
import com.playground.common.dto.PageResponse;
import com.playground.common.api.ApiHeaders;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/fraud")
@Tag(name = "Fraud", description = "Fraud screening and alert management")
@SecurityRequirement(name = "bearerAuth")
public class FraudController {

    private final FraudService fraudService;

    public FraudController(FraudService fraudService) {
        this.fraudService = fraudService;
    }

    @PostMapping("/screen")
    @Operation(summary = "Screen transaction for fraud")
    public ResponseEntity<FraudAlertResponse> screen(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @Valid @RequestBody FraudScreenRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        if (request.partyId() != null) {
            TenantAccess.requirePartyAccess(jwt, request.partyId());
        }
        FraudAlertResponse alert = fraudService.screen(tenantId, jwt.getSubject(), request);
        if (alert == null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(alert);
    }

    @GetMapping("/alerts")
    @Operation(summary = "List fraud alerts")
    public PageResponse<FraudAlertResponse> listAlerts(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestParam(required = false) String partyId,
            @RequestParam(required = false) FraudAlertStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        if (partyId != null) {
            TenantAccess.requirePartyAccess(jwt, partyId);
        } else {
            TenantAccess.requirePrivileged(jwt);
        }
        return fraudService.listAlerts(tenantId, partyId, status, page, size);
    }

    @PatchMapping("/alerts/{alertId}/review")
    @Operation(summary = "Review fraud alert (privileged)")
    public FraudAlertResponse reviewAlert(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String alertId,
            @Valid @RequestBody ReviewFraudAlertRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePrivileged(jwt);
        return fraudService.reviewAlert(tenantId, jwt.getSubject(), alertId, request);
    }
}
