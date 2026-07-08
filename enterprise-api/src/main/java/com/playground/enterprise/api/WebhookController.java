package com.playground.enterprise.api;

import com.playground.common.security.TenantAccess;
import com.playground.common.dto.PageResponse;
import com.playground.enterprise.webhook.dto.RegisterWebhookRequest;
import com.playground.enterprise.webhook.dto.WebhookDeliveryResponse;
import com.playground.enterprise.webhook.dto.WebhookResponse;
import com.playground.enterprise.webhook.service.WebhookService;
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
@RequestMapping("/v1/webhooks")
@Tag(name = "Webhooks", description = "Webhook registration and delivery tracking")
@SecurityRequirement(name = "bearerAuth")
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register webhook")
    public WebhookResponse register(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @Valid @RequestBody RegisterWebhookRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePrivileged(jwt);
        return webhookService.register(tenantId, request);
    }

    @GetMapping
    @Operation(summary = "List webhooks")
    public PageResponse<WebhookResponse> list(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePrivileged(jwt);
        return webhookService.list(tenantId);
    }

    @GetMapping("/{webhookId}/deliveries")
    @Operation(summary = "List webhook deliveries")
    public PageResponse<WebhookDeliveryResponse> listDeliveries(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String webhookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePrivileged(jwt);
        return webhookService.listDeliveries(tenantId, webhookId, page, size);
    }
}
