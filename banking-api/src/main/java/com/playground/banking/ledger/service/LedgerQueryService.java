package com.playground.banking.ledger.service;

import com.playground.banking.account.repository.AccountRepository;
import com.playground.banking.ledger.dto.LedgerEntryResponse;
import com.playground.banking.ledger.entity.LedgerEntryEntity;
import com.playground.banking.ledger.repository.LedgerEntryRepository;
import com.playground.common.dto.PageResponse;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class LedgerQueryService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final AccountRepository accountRepository;

    public LedgerQueryService(LedgerEntryRepository ledgerEntryRepository, AccountRepository accountRepository) {
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<LedgerEntryResponse> listEntries(
            String tenantId,
            String accountId,
            LocalDate from,
            LocalDate to,
            int page,
            int size) {
        accountRepository.findByIdAndTenantId(accountId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        Page<LedgerEntryEntity> result;
        if (from != null && to != null) {
            Instant fromInstant = from.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant toInstant = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
            result = ledgerEntryRepository.findByTenantIdAndAccountIdAndCreatedAtBetween(
                    tenantId, accountId, fromInstant, toInstant, PageRequest.of(page, size));
        } else {
            result = ledgerEntryRepository.findByTenantIdAndAccountId(
                    tenantId, accountId, PageRequest.of(page, size));
        }

        return new PageResponse<>(
                result.getContent().stream().map(LedgerQueryService::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), null);
    }

    static LedgerEntryResponse toResponse(LedgerEntryEntity entry) {
        return new LedgerEntryResponse(
                entry.getId(), entry.getTransactionId(), entry.getAccountId(),
                entry.getEntryType(), entry.getAmount(), entry.getCurrency(),
                entry.getBalanceAfter(), entry.getCreatedAt());
    }
}
