package com.playground.banking.api;

import com.playground.banking.account.dto.AccountResponse;
import com.playground.banking.account.service.AccountService;
import com.playground.banking.idempotency.service.IdempotencyService;
import com.playground.common.security.TenantAccess;
import com.playground.common.dto.PageResponse;
import com.playground.banking.transfer.dto.CreateTransferRequest;
import com.playground.banking.transfer.dto.TransferResponse;
import com.playground.banking.transfer.service.TransferService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transfers")
@Tag(name = "Transfers", description = "Internal account-to-account transfers")
@SecurityRequirement(name = "bearerAuth")
public class TransferController {

    private final TransferService transferService;
    private final AccountService accountService;
    private final IdempotencyService idempotencyService;

    public TransferController(
            TransferService transferService,
            AccountService accountService,
            IdempotencyService idempotencyService) {
        this.transferService = transferService;
        this.accountService = accountService;
        this.idempotencyService = idempotencyService;
    }

    @PostMapping
    @Operation(summary = "Create transfer")
    public ResponseEntity<TransferResponse> createTransfer(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestHeader(ApiHeaders.IDEMPOTENCY_KEY) String idempotencyKey,
            @Valid @RequestBody CreateTransferRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        AccountResponse from = accountService.getAccount(tenantId, request.fromAccountId());
        TenantAccess.requirePartyAccess(jwt, from.partyId());
        String actorId = jwt.getSubject();

        IdempotencyService.IdempotencyOutcome<TransferResponse> outcome = idempotencyService.execute(
                tenantId, idempotencyKey, "CREATE_TRANSFER", request,
                () -> transferService.createTransfer(tenantId, actorId, idempotencyKey, request),
                TransferResponse.class, HttpStatus.CREATED);

        return ResponseEntity.status(outcome.statusCode()).body(outcome.body());
    }

    @GetMapping("/{transferId}")
    @Operation(summary = "Get transfer by id")
    public TransferResponse getTransfer(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String transferId) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TransferResponse transfer = transferService.getTransfer(tenantId, transferId);
        AccountResponse from = accountService.getAccount(tenantId, transfer.fromAccountId());
        TenantAccess.requirePartyAccess(jwt, from.partyId());
        return transfer;
    }

    @GetMapping
    @Operation(summary = "List transfers from an account")
    public PageResponse<TransferResponse> listTransfers(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestParam String accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        AccountResponse account = accountService.getAccount(tenantId, accountId);
        TenantAccess.requirePartyAccess(jwt, account.partyId());
        return transferService.listByAccount(tenantId, accountId, page, size);
    }
}
