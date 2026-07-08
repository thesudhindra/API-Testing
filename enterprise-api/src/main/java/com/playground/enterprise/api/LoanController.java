package com.playground.enterprise.api;

import com.playground.enterprise.domain.LoanStatus;
import com.playground.enterprise.loan.dto.CreateLoanRequest;
import com.playground.enterprise.loan.dto.CreateRepaymentRequest;
import com.playground.enterprise.loan.dto.LoanResponse;
import com.playground.enterprise.loan.dto.RepaymentResponse;
import com.playground.enterprise.loan.service.LoanService;
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
@RequestMapping("/v1/loans")
@Tag(name = "Loans", description = "Loan origination and repayment")
@SecurityRequirement(name = "bearerAuth")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create loan")
    public LoanResponse createLoan(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @Valid @RequestBody CreateLoanRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePartyAccess(jwt, request.partyId());
        return loanService.createLoan(tenantId, jwt.getSubject(), request);
    }

    @GetMapping("/{loanId}")
    @Operation(summary = "Get loan by id")
    public LoanResponse getLoan(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String loanId) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        LoanResponse loan = loanService.getLoan(tenantId, loanId);
        TenantAccess.requirePartyAccess(jwt, loan.partyId());
        return loan;
    }

    @GetMapping
    @Operation(summary = "List loans for a party")
    public PageResponse<LoanResponse> listLoans(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @RequestParam String partyId,
            @RequestParam(required = false) LoanStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        TenantAccess.requirePartyAccess(jwt, partyId);
        return loanService.listLoans(tenantId, partyId, status, page, size);
    }

    @PostMapping("/{loanId}/repayments")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Repay loan")
    public RepaymentResponse repayLoan(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = ApiHeaders.TENANT_ID, required = false) String tenantHeader,
            @PathVariable String loanId,
            @Valid @RequestBody CreateRepaymentRequest request) {
        String tenantId = TenantAccess.requireTenantHeader(tenantHeader, jwt);
        LoanResponse loan = loanService.getLoan(tenantId, loanId);
        TenantAccess.requirePartyAccess(jwt, loan.partyId());
        return loanService.repayLoan(tenantId, jwt.getSubject(), loanId, request);
    }
}
