package com.playground.banking.api;

import com.playground.banking.account.dto.AccountResponse;
import com.playground.banking.account.service.AccountService;
import com.playground.banking.statement.dto.GenerateStatementRequest;
import com.playground.banking.statement.dto.StatementResponse;
import com.playground.banking.statement.service.StatementService;
import com.playground.common.security.TenantAccess;
import com.playground.common.dto.PageResponse;
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
@Tag(name = "Statements", description = "Account statements")
@SecurityRequirement(name = "bearerAuth")
public class StatementController {

    private final StatementService statementService;
    private final AccountService accountService;

    public StatementController(StatementService statementService, AccountService accountService) {
        this.statementService = statementService;
        this.accountService = accountService;
    }

    @PostMapping("/v1/accounts/{accountId}/statements")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Generate statement for an account")
    public StatementResponse generateStatement(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String accountId,
            @Valid @RequestBody GenerateStatementRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        AccountResponse account = accountService.getAccount(tenantId, accountId);
        TenantAccess.requirePartyAccess(jwt, account.partyId());
        return statementService.generate(tenantId, jwt.getSubject(), accountId, request);
    }

    @GetMapping("/v1/accounts/{accountId}/statements")
    @Operation(summary = "List statements for an account")
    public PageResponse<StatementResponse> listStatements(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        AccountResponse account = accountService.getAccount(tenantId, accountId);
        TenantAccess.requirePartyAccess(jwt, account.partyId());
        return statementService.listByAccount(tenantId, accountId, page, size);
    }

    @GetMapping("/v1/statements/{statementId}")
    @Operation(summary = "Get statement with lines")
    public StatementResponse getStatement(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String statementId) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        StatementResponse statement = statementService.getStatement(tenantId, statementId);
        AccountResponse account = accountService.getAccount(tenantId, statement.accountId());
        TenantAccess.requirePartyAccess(jwt, account.partyId());
        return statement;
    }
}
