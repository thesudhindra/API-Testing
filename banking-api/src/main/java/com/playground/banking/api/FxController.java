package com.playground.banking.api;

import com.playground.banking.fx.dto.CreateFxQuoteRequest;
import com.playground.banking.fx.dto.ExecuteFxConversionRequest;
import com.playground.banking.fx.dto.FxConversionResponse;
import com.playground.banking.fx.dto.FxQuoteResponse;
import com.playground.banking.fx.service.FxService;
import com.playground.banking.idempotency.service.IdempotencyService;
import com.playground.common.security.TenantAccess;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/fx")
@Tag(name = "FX", description = "Foreign exchange quotes and conversions")
@SecurityRequirement(name = "bearerAuth")
public class FxController {

    private final FxService fxService;
    private final IdempotencyService idempotencyService;

    public FxController(FxService fxService, IdempotencyService idempotencyService) {
        this.fxService = fxService;
        this.idempotencyService = idempotencyService;
    }

    @PostMapping("/quotes")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create FX quote")
    public FxQuoteResponse createQuote(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @Valid @RequestBody CreateFxQuoteRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        return fxService.createQuote(tenantId, request);
    }

    @PostMapping("/conversions")
    @Operation(summary = "Execute FX conversion")
    public ResponseEntity<FxConversionResponse> executeConversion(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestHeader(ApiHeaders.IDEMPOTENCY_KEY) String idempotencyKey,
            @Valid @RequestBody ExecuteFxConversionRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePartyAccess(jwt, request.partyId());

        IdempotencyService.IdempotencyOutcome<FxConversionResponse> outcome = idempotencyService.execute(
                tenantId, idempotencyKey, "FX_CONVERSION", request,
                () -> fxService.executeConversion(tenantId, jwt.getSubject(), idempotencyKey, request),
                FxConversionResponse.class, HttpStatus.CREATED);

        return ResponseEntity.status(outcome.statusCode()).body(outcome.body());
    }

    @GetMapping("/conversions/{conversionId}")
    @Operation(summary = "Get FX conversion by id")
    public FxConversionResponse getConversion(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String conversionId) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        return fxService.getConversion(tenantId, conversionId);
    }
}
