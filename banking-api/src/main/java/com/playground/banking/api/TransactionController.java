package com.playground.banking.api;

import com.playground.banking.account.dto.AccountResponse;
import com.playground.banking.account.service.AccountService;
import com.playground.banking.domain.TransactionStatus;
import com.playground.banking.domain.TransactionType;
import com.playground.common.security.TenantAccess;
import com.playground.common.dto.PageResponse;
import com.playground.banking.transaction.dto.TransactionResponse;
import com.playground.banking.transaction.service.TransactionService;
import com.playground.common.api.ApiHeaders;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1/transactions")
@Tag(name = "Transactions", description = "Financial transaction history")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;

    public TransactionController(TransactionService transactionService, AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "Get transaction by id")
    public TransactionResponse getTransaction(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String transactionId) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TransactionResponse txn = transactionService.getTransaction(tenantId, transactionId);
        TenantAccess.requirePartyAccess(jwt, txn.partyId());
        return txn;
    }

    @GetMapping
    @Operation(summary = "List transactions for an account")
    public PageResponse<TransactionResponse> listTransactions(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestParam String accountId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        AccountResponse account = accountService.getAccount(tenantId, accountId);
        TenantAccess.requirePartyAccess(jwt, account.partyId());
        return transactionService.listTransactions(tenantId, accountId, type, status, from, to, page, size);
    }
}
