package com.playground.enterprise.api;

import com.playground.enterprise.domain.EventStatus;
import com.playground.enterprise.event.dto.EventResponse;
import com.playground.enterprise.event.service.EventOutboxService;
import com.playground.common.security.TenantAccess;
import com.playground.common.dto.PageResponse;
import com.playground.common.api.ApiHeaders;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/events")
@Tag(name = "Events", description = "Domain event outbox")
@SecurityRequirement(name = "bearerAuth")
public class EventController {

    private final EventOutboxService eventOutboxService;

    public EventController(EventOutboxService eventOutboxService) {
        this.eventOutboxService = eventOutboxService;
    }

    @GetMapping
    @Operation(summary = "List domain events (privileged)")
    public PageResponse<EventResponse> listEvents(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestParam(required = false) EventStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePrivileged(jwt);
        return eventOutboxService.listEvents(tenantId, status, page, size);
    }
}
