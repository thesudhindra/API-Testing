package com.playground.banking.transfer.service;

import com.playground.banking.account.entity.AccountEntity;
import com.playground.banking.account.repository.AccountRepository;
import com.playground.banking.audit.service.AuditService;
import com.playground.banking.customer.service.PartyService;
import com.playground.banking.domain.TransactionStatus;
import com.playground.banking.domain.TransactionType;
import com.playground.banking.domain.TransferStatus;
import com.playground.banking.ledger.service.LedgerService;
import com.playground.common.dto.PageResponse;
import com.playground.banking.transaction.entity.FinancialTransactionEntity;
import com.playground.banking.transaction.repository.FinancialTransactionRepository;
import com.playground.banking.transfer.dto.CreateTransferRequest;
import com.playground.banking.transfer.dto.TransferResponse;
import com.playground.banking.transfer.entity.TransferEntity;
import com.playground.banking.transfer.repository.TransferRepository;
import com.playground.common.exception.BadRequestException;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final FinancialTransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final PartyService partyService;
    private final LedgerService ledgerService;
    private final AuditService auditService;

    public TransferService(
            TransferRepository transferRepository,
            FinancialTransactionRepository transactionRepository,
            AccountRepository accountRepository,
            PartyService partyService,
            LedgerService ledgerService,
            AuditService auditService) {
        this.transferRepository = transferRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.partyService = partyService;
        this.ledgerService = ledgerService;
        this.auditService = auditService;
    }

    @Transactional
    public TransferResponse createTransfer(String tenantId, String actorId, String idempotencyKey, CreateTransferRequest request) {
        if (request.fromAccountId().equals(request.toAccountId())) {
            throw new BadRequestException("Cannot transfer to the same account");
        }

        AccountEntity from = accountRepository.findByIdAndTenantId(request.fromAccountId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found"));
        AccountEntity to = accountRepository.findByIdAndTenantId(request.toAccountId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Destination account not found"));

        if (!from.getCurrency().equals(request.currency()) || !to.getCurrency().equals(request.currency())) {
            throw new BadRequestException("Currency mismatch between accounts and request");
        }

        partyService.requirePartyInTenant(tenantId, from.getPartyId());

        Instant now = Instant.now();
        String txnId = UUID.randomUUID().toString();

        FinancialTransactionEntity txn = new FinancialTransactionEntity();
        txn.setId(txnId);
        txn.setTenantId(tenantId);
        txn.setPartyId(from.getPartyId());
        txn.setAccountId(from.getId());
        txn.setTxnType(TransactionType.TRANSFER);
        txn.setStatus(TransactionStatus.COMPLETED);
        txn.setAmount(request.amount());
        txn.setCurrency(request.currency());
        txn.setReference(request.reference());
        txn.setDescription("Transfer to " + to.getAccountNumber());
        txn.setIdempotencyKey(idempotencyKey);
        txn.setCreatedAt(now);
        txn.setUpdatedAt(now);
        transactionRepository.save(txn);

        ledgerService.debit(tenantId, from.getId(), request.amount(), request.currency(), txnId);
        ledgerService.credit(tenantId, to.getId(), request.amount(), request.currency(), txnId);

        TransferEntity transfer = new TransferEntity();
        transfer.setId(UUID.randomUUID().toString());
        transfer.setTenantId(tenantId);
        transfer.setTransactionId(txnId);
        transfer.setFromAccountId(from.getId());
        transfer.setToAccountId(to.getId());
        transfer.setAmount(request.amount());
        transfer.setCurrency(request.currency());
        transfer.setReference(request.reference());
        transfer.setStatus(TransferStatus.COMPLETED);
        transfer.setCreatedAt(now);
        transferRepository.save(transfer);

        auditService.record(tenantId, "TRANSFER", transfer.getId(), "CREATED", actorId,
                "from=" + from.getId() + " to=" + to.getId());

        return toResponse(transfer);
    }

    @Transactional(readOnly = true)
    public TransferResponse getTransfer(String tenantId, String transferId) {
        TransferEntity transfer = transferRepository.findByIdAndTenantId(transferId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer not found"));
        return toResponse(transfer);
    }

    @Transactional(readOnly = true)
    public PageResponse<TransferResponse> listByAccount(String tenantId, String accountId, int page, int size) {
        Page<TransferEntity> result = transferRepository.findByTenantIdAndFromAccountId(
                tenantId, accountId, PageRequest.of(page, size));
        return new PageResponse<>(
                result.getContent().stream().map(TransferService::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), null);
    }

    static TransferResponse toResponse(TransferEntity transfer) {
        return new TransferResponse(
                transfer.getId(), transfer.getTransactionId(), transfer.getFromAccountId(),
                transfer.getToAccountId(), transfer.getAmount(), transfer.getCurrency(),
                transfer.getReference(), transfer.getStatus(), transfer.getCreatedAt());
    }
}
