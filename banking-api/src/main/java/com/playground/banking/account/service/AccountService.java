package com.playground.banking.account.service;

import com.playground.banking.account.dto.AccountResponse;
import com.playground.banking.account.dto.CreateAccountRequest;
import com.playground.banking.account.entity.AccountEntity;
import com.playground.banking.account.repository.AccountRepository;
import com.playground.banking.customer.service.PartyService;
import com.playground.banking.domain.AccountStatus;
import com.playground.banking.kyc.service.KycService;
import com.playground.common.dto.PageResponse;
import com.playground.common.exception.BadRequestException;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PartyService partyService;
    private final KycService kycService;

    public AccountService(
            AccountRepository accountRepository,
            PartyService partyService,
            KycService kycService) {
        this.accountRepository = accountRepository;
        this.partyService = partyService;
        this.kycService = kycService;
    }

    @Transactional(readOnly = true)
    public PageResponse<AccountResponse> listByParty(String tenantId, String partyId, int page, int size) {
        partyService.requirePartyInTenant(tenantId, partyId);
        Page<AccountEntity> result = accountRepository.findByTenantIdAndPartyId(
                tenantId, partyId, PageRequest.of(page, size));
        return new PageResponse<>(
                result.getContent().stream().map(AccountService::toResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                null);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(String tenantId, String accountId) {
        AccountEntity account = accountRepository.findByIdAndTenantId(accountId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        return toResponse(account);
    }

    @Transactional
    public AccountResponse createAccount(String tenantId, CreateAccountRequest request) {
        partyService.requirePartyInTenant(tenantId, request.partyId());

        if (!kycService.hasApprovedKyc(tenantId, request.partyId())) {
            throw new BadRequestException("Party must have approved KYC before opening an account");
        }

        AccountEntity account = new AccountEntity();
        account.setId("acct-" + java.util.UUID.randomUUID());
        account.setTenantId(tenantId);
        account.setPartyId(request.partyId());
        account.setAccountNumber(generateAccountNumber());
        account.setProductCode(request.productCode());
        account.setCurrency(request.currency());
        account.setStatus(AccountStatus.ACTIVE);
        account.setAvailableBalance(BigDecimal.ZERO);
        account.setLedgerBalance(BigDecimal.ZERO);
        account.setCreatedAt(Instant.now());

        return toResponse(accountRepository.save(account));
    }

    private static String generateAccountNumber() {
        long suffix = ThreadLocalRandom.current().nextLong(1_000_000_000L, 9_999_999_999L);
        return "GB" + suffix;
    }

    static AccountResponse toResponse(AccountEntity account) {
        return new AccountResponse(
                account.getId(),
                account.getTenantId(),
                account.getPartyId(),
                account.getAccountNumber(),
                account.getCurrency(),
                account.getProductCode(),
                account.getStatus(),
                account.getAvailableBalance(),
                account.getLedgerBalance(),
                account.getVersion(),
                account.getCreatedAt());
    }
}
