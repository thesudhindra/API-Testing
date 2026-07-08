package com.playground.banking.statement.service;

import com.playground.banking.account.entity.AccountEntity;
import com.playground.banking.account.repository.AccountRepository;
import com.playground.banking.audit.service.AuditService;
import com.playground.banking.domain.LedgerEntryType;
import com.playground.banking.domain.StatementStatus;
import com.playground.banking.ledger.entity.LedgerEntryEntity;
import com.playground.banking.ledger.repository.LedgerEntryRepository;
import com.playground.banking.statement.dto.GenerateStatementRequest;
import com.playground.banking.statement.dto.StatementLineResponse;
import com.playground.banking.statement.dto.StatementResponse;
import com.playground.banking.statement.entity.StatementEntity;
import com.playground.banking.statement.entity.StatementLineEntity;
import com.playground.banking.statement.repository.StatementLineRepository;
import com.playground.banking.statement.repository.StatementRepository;
import com.playground.common.dto.PageResponse;
import com.playground.banking.transaction.entity.FinancialTransactionEntity;
import com.playground.banking.transaction.repository.FinancialTransactionRepository;
import com.playground.common.exception.BadRequestException;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StatementService {

    private final StatementRepository statementRepository;
    private final StatementLineRepository statementLineRepository;
    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final FinancialTransactionRepository transactionRepository;
    private final AuditService auditService;

    public StatementService(
            StatementRepository statementRepository,
            StatementLineRepository statementLineRepository,
            AccountRepository accountRepository,
            LedgerEntryRepository ledgerEntryRepository,
            FinancialTransactionRepository transactionRepository,
            AuditService auditService) {
        this.statementRepository = statementRepository;
        this.statementLineRepository = statementLineRepository;
        this.accountRepository = accountRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.transactionRepository = transactionRepository;
        this.auditService = auditService;
    }

    @Transactional
    public StatementResponse generate(
            String tenantId, String actorId, String accountId, GenerateStatementRequest request) {
        if (request.periodEnd().isBefore(request.periodStart())) {
            throw new BadRequestException("periodEnd must be on or after periodStart");
        }

        AccountEntity account = accountRepository.findByIdAndTenantId(accountId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        Instant periodStart = request.periodStart().atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant periodEnd = request.periodEnd().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        BigDecimal openingBalance = ledgerEntryRepository
                .findTopByTenantIdAndAccountIdAndCreatedAtBeforeOrderByCreatedAtDesc(tenantId, accountId, periodStart)
                .map(LedgerEntryEntity::getBalanceAfter)
                .orElse(account.getLedgerBalance());

        List<LedgerEntryEntity> entries = ledgerEntryRepository
                .findByTenantIdAndAccountIdAndCreatedAtBetween(
                        tenantId, accountId, periodStart, periodEnd,
                        PageRequest.of(0, 10_000, Sort.by("createdAt").ascending()))
                .getContent();

        Map<String, FinancialTransactionEntity> txns = transactionRepository.findAllById(
                        entries.stream().map(LedgerEntryEntity::getTransactionId).distinct().toList())
                .stream()
                .collect(Collectors.toMap(FinancialTransactionEntity::getId, Function.identity()));

        BigDecimal closingBalance = entries.isEmpty()
                ? openingBalance
                : entries.get(entries.size() - 1).getBalanceAfter();

        Instant now = Instant.now();
        StatementEntity statement = new StatementEntity();
        statement.setId(UUID.randomUUID().toString());
        statement.setTenantId(tenantId);
        statement.setAccountId(accountId);
        statement.setPeriodStart(request.periodStart());
        statement.setPeriodEnd(request.periodEnd());
        statement.setOpeningBalance(openingBalance);
        statement.setClosingBalance(closingBalance);
        statement.setCurrency(account.getCurrency());
        statement.setStatus(StatementStatus.PUBLISHED);
        statement.setCreatedAt(now);
        statementRepository.save(statement);

        List<StatementLineEntity> lines = new ArrayList<>();
        int sortOrder = 0;
        for (LedgerEntryEntity entry : entries) {
            FinancialTransactionEntity txn = txns.get(entry.getTransactionId());
            String description = txn != null ? txn.getDescription() : entry.getEntryType().name();

            BigDecimal signedAmount = entry.getEntryType() == LedgerEntryType.CREDIT
                    ? entry.getAmount()
                    : entry.getAmount().negate();

            StatementLineEntity line = new StatementLineEntity();
            line.setId(UUID.randomUUID().toString());
            line.setStatementId(statement.getId());
            line.setTransactionId(entry.getTransactionId());
            line.setPostedAt(entry.getCreatedAt());
            line.setDescription(description);
            line.setAmount(signedAmount);
            line.setBalanceAfter(entry.getBalanceAfter());
            line.setSortOrder(sortOrder++);
            lines.add(line);
        }
        statementLineRepository.saveAll(lines);

        auditService.record(tenantId, "STATEMENT", statement.getId(), "GENERATED", actorId,
                "account=" + accountId + " period=" + request.periodStart() + ".." + request.periodEnd());

        return toResponse(statement, lines);
    }

    @Transactional(readOnly = true)
    public StatementResponse getStatement(String tenantId, String statementId) {
        StatementEntity statement = statementRepository.findByIdAndTenantId(statementId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Statement not found"));
        List<StatementLineEntity> lines = statementLineRepository.findByStatementIdOrderBySortOrderAsc(statementId);
        return toResponse(statement, lines);
    }

    @Transactional(readOnly = true)
    public PageResponse<StatementResponse> listByAccount(String tenantId, String accountId, int page, int size) {
        accountRepository.findByIdAndTenantId(accountId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        Page<StatementEntity> result = statementRepository.findByTenantIdAndAccountId(
                tenantId, accountId, PageRequest.of(page, size));

        return new PageResponse<>(
                result.getContent().stream()
                        .map(s -> toResponse(s, List.of()))
                        .toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), null);
    }

    static StatementResponse toResponse(StatementEntity statement, List<StatementLineEntity> lines) {
        List<StatementLineResponse> lineResponses = lines.stream()
                .map(line -> new StatementLineResponse(
                        line.getId(), line.getTransactionId(), line.getPostedAt(),
                        line.getDescription(), line.getAmount(), line.getBalanceAfter(), line.getSortOrder()))
                .toList();

        return new StatementResponse(
                statement.getId(), statement.getAccountId(),
                statement.getPeriodStart(), statement.getPeriodEnd(),
                statement.getOpeningBalance(), statement.getClosingBalance(),
                statement.getCurrency(), statement.getStatus(), statement.getCreatedAt(), lineResponses);
    }
}
