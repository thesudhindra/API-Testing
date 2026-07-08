package com.playground.banking.transaction.service;

import com.playground.banking.account.entity.AccountEntity;
import com.playground.banking.account.repository.AccountRepository;
import com.playground.banking.domain.TransactionStatus;
import com.playground.banking.domain.TransactionType;
import com.playground.common.dto.PageResponse;
import com.playground.banking.transaction.dto.TransactionResponse;
import com.playground.banking.transaction.entity.FinancialTransactionEntity;
import com.playground.banking.transaction.repository.FinancialTransactionRepository;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class TransactionService {

    private final FinancialTransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(
            FinancialTransactionRepository transactionRepository,
            AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(String tenantId, String transactionId) {
        FinancialTransactionEntity txn = transactionRepository.findByIdAndTenantId(transactionId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        return toResponse(txn);
    }

    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> listTransactions(
            String tenantId,
            String accountId,
            TransactionType txnType,
            TransactionStatus status,
            LocalDate from,
            LocalDate to,
            int page,
            int size) {
        requireAccount(tenantId, accountId);

        Instant fromInstant = from != null ? from.atStartOfDay(ZoneOffset.UTC).toInstant() : null;
        Instant toInstant = to != null ? to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant() : null;

        Page<FinancialTransactionEntity> result = transactionRepository.search(
                tenantId, accountId, txnType, status, fromInstant, toInstant, PageRequest.of(page, size));

        return new PageResponse<>(
                result.getContent().stream().map(TransactionService::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), null);
    }

    private void requireAccount(String tenantId, String accountId) {
        accountRepository.findByIdAndTenantId(accountId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    static TransactionResponse toResponse(FinancialTransactionEntity txn) {
        return new TransactionResponse(
                txn.getId(), txn.getPartyId(), txn.getAccountId(),
                txn.getTxnType(), txn.getStatus(), txn.getAmount(),
                txn.getCurrency(), txn.getReference(), txn.getDescription(), txn.getCreatedAt());
    }
}
