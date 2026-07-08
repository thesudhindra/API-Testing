package com.playground.banking.api;

import com.playground.banking.idempotency.service.IdempotencyService;
import com.playground.banking.payment.dto.CreatePaymentRequest;
import com.playground.banking.payment.dto.PaymentResponse;
import com.playground.banking.payment.service.PaymentService;
import com.playground.common.security.TenantAccess;
import com.playground.common.dto.PageResponse;
import com.playground.common.api.ApiHeaders;
import com.playground.banking.domain.PaymentStatus;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/payments")
@Tag(name = "Payments", description = "Outbound payments to beneficiaries")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;
    private final IdempotencyService idempotencyService;

    public PaymentController(PaymentService paymentService, IdempotencyService idempotencyService) {
        this.paymentService = paymentService;
        this.idempotencyService = idempotencyService;
    }

    @PostMapping
    @Operation(summary = "Create payment")
    public ResponseEntity<PaymentResponse> createPayment(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestHeader(ApiHeaders.IDEMPOTENCY_KEY) String idempotencyKey,
            @Valid @RequestBody CreatePaymentRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePartyAccess(jwt, request.partyId());
        String actorId = jwt.getSubject();

        IdempotencyService.IdempotencyOutcome<PaymentResponse> outcome = idempotencyService.execute(
                tenantId, idempotencyKey, "CREATE_PAYMENT", request,
                () -> paymentService.createPayment(tenantId, actorId, idempotencyKey, request),
                PaymentResponse.class, HttpStatus.CREATED);

        return ResponseEntity.status(outcome.statusCode()).body(outcome.body());
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by id")
    public PaymentResponse getPayment(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String paymentId) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        PaymentResponse payment = paymentService.getPayment(tenantId, paymentId);
        TenantAccess.requirePartyAccess(jwt, payment.partyId());
        return payment;
    }

    @GetMapping
    @Operation(summary = "List payments for a party")
    public PageResponse<PaymentResponse> listPayments(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestParam String partyId,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePartyAccess(jwt, partyId);
        return paymentService.listPayments(tenantId, partyId, status, page, size);
    }
}
