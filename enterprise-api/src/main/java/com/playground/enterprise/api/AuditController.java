package com.playground.enterprise.api;

import com.playground.enterprise.audit.dto.AuditEventResponse;
import com.playground.enterprise.audit.service.EnterpriseAuditService;
import com.playground.common.security.TenantAccess;
import com.playground.common.dto.PageResponse;
import com.playground.common.api.ApiHeaders;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/v1/audit/events")
@Tag(name = "Audit", description = "Audit event query")
@SecurityRequirement(name = "bearerAuth")
public class AuditController {

    private final EnterpriseAuditService auditService;

    public AuditController(EnterpriseAuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @Operation(summary = "Query audit events (privileged)")
    public PageResponse<AuditEventResponse> queryEvents(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String correlationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePrivileged(jwt);
        return auditService.query(tenantId, entityType, correlationId, from, to, page, size);
    }
}
