package com.playground.banking.ledger.service;

import com.playground.banking.account.entity.AccountEntity;
import com.playground.banking.account.repository.AccountRepository;
import com.playground.banking.domain.AccountStatus;
import com.playground.banking.domain.LedgerEntryType;
import com.playground.banking.ledger.entity.LedgerEntryEntity;
import com.playground.banking.ledger.repository.LedgerEntryRepository;
import com.playground.common.exception.BadRequestException;
import com.playground.common.exception.ConflictException;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class LedgerService {

    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public LedgerService(AccountRepository accountRepository, LedgerEntryRepository ledgerEntryRepository) {
        this.accountRepository = accountRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Transactional
    public LedgerPostResult debit(String tenantId, String accountId, BigDecimal amount, String currency, String transactionId) {
        return post(tenantId, accountId, amount, currency, transactionId, LedgerEntryType.DEBIT, true);
    }

    @Transactional
    public LedgerPostResult credit(String tenantId, String accountId, BigDecimal amount, String currency, String transactionId) {
        return post(tenantId, accountId, amount, currency, transactionId, LedgerEntryType.CREDIT, false);
    }

    private LedgerPostResult post(
            String tenantId,
            String accountId,
            BigDecimal amount,
            String currency,
            String transactionId,
            LedgerEntryType entryType,
            boolean isDebit) {
        if (amount == null || amount.signum() <= 0) {
            throw new BadRequestException("Amount must be positive");
        }

        AccountEntity account = accountRepository.findByIdAndTenantId(accountId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Account is not active");
        }
        if (!account.getCurrency().equals(currency)) {
            throw new BadRequestException("Currency mismatch for account");
        }

        BigDecimal newBalance = isDebit
                ? account.getLedgerBalance().subtract(amount)
                : account.getLedgerBalance().add(amount);

        if (isDebit && newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Insufficient funds");
        }

        account.setLedgerBalance(newBalance);
        account.setAvailableBalance(newBalance);

        try {
            accountRepository.save(account);
        } catch (OptimisticLockingFailureException ex) {
            throw new ConflictException("Account balance was modified concurrently; retry the operation");
        }

        LedgerEntryEntity entry = new LedgerEntryEntity();
        entry.setId(UUID.randomUUID().toString());
        entry.setTenantId(tenantId);
        entry.setTransactionId(transactionId);
        entry.setAccountId(accountId);
        entry.setEntryType(entryType);
        entry.setAmount(amount);
        entry.setCurrency(currency);
        entry.setBalanceAfter(newBalance);
        entry.setCreatedAt(Instant.now());
        ledgerEntryRepository.save(entry);

        return new LedgerPostResult(entry.getId(), newBalance);
    }

    public record LedgerPostResult(String ledgerEntryId, BigDecimal balanceAfter) {
    }
}
