package com.playground.banking.api;

import com.playground.banking.account.dto.AccountResponse;
import com.playground.banking.account.service.AccountService;
import com.playground.banking.ledger.dto.LedgerEntryResponse;
import com.playground.banking.ledger.service.LedgerQueryService;
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

import java.time.LocalDate;

@RestController
@RequestMapping("/v1/ledger-entries")
@Tag(name = "Ledger", description = "Account ledger entries")
@SecurityRequirement(name = "bearerAuth")
public class LedgerController {

    private final LedgerQueryService ledgerQueryService;
    private final AccountService accountService;

    public LedgerController(LedgerQueryService ledgerQueryService, AccountService accountService) {
        this.ledgerQueryService = ledgerQueryService;
        this.accountService = accountService;
    }

    @GetMapping
    @Operation(summary = "List ledger entries for an account")
    public PageResponse<LedgerEntryResponse> listLedgerEntries(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestParam String accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        AccountResponse account = accountService.getAccount(tenantId, accountId);
        TenantAccess.requirePartyAccess(jwt, account.partyId());
        return ledgerQueryService.listEntries(tenantId, accountId, from, to, page, size);
    }
}
