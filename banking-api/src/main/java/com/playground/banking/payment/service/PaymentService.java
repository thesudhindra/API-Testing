package com.playground.banking.payment.service;

import com.playground.banking.account.entity.AccountEntity;
import com.playground.banking.account.repository.AccountRepository;
import com.playground.banking.audit.service.AuditService;
import com.playground.banking.beneficiary.entity.BeneficiaryEntity;
import com.playground.banking.beneficiary.repository.BeneficiaryRepository;
import com.playground.banking.customer.service.PartyService;
import com.playground.banking.domain.BeneficiaryStatus;
import com.playground.banking.domain.PaymentStatus;
import com.playground.banking.domain.TransactionStatus;
import com.playground.banking.domain.TransactionType;
import com.playground.banking.ledger.service.LedgerService;
import com.playground.banking.payment.dto.CreatePaymentRequest;
import com.playground.banking.payment.dto.PaymentResponse;
import com.playground.banking.payment.entity.PaymentEntity;
import com.playground.banking.payment.repository.PaymentRepository;
import com.playground.banking.transaction.entity.FinancialTransactionEntity;
import com.playground.banking.transaction.repository.FinancialTransactionRepository;
import com.playground.common.exception.BadRequestException;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.playground.common.dto.PageResponse;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final FinancialTransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final PartyService partyService;
    private final LedgerService ledgerService;
    private final AuditService auditService;

    public PaymentService(
            PaymentRepository paymentRepository,
            FinancialTransactionRepository transactionRepository,
            AccountRepository accountRepository,
            BeneficiaryRepository beneficiaryRepository,
            PartyService partyService,
            LedgerService ledgerService,
            AuditService auditService) {
        this.paymentRepository = paymentRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.partyService = partyService;
        this.ledgerService = ledgerService;
        this.auditService = auditService;
    }

    @Transactional
    public PaymentResponse createPayment(String tenantId, String actorId, String idempotencyKey, CreatePaymentRequest request) {
        partyService.requirePartyInTenant(tenantId, request.partyId());

        AccountEntity account = accountRepository.findByIdAndTenantId(request.accountId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        if (!account.getPartyId().equals(request.partyId())) {
            throw new BadRequestException("Account does not belong to party");
        }

        BeneficiaryEntity beneficiary = beneficiaryRepository.findById(request.beneficiaryId())
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found"));
        if (!beneficiary.getTenantId().equals(tenantId) || !beneficiary.getPartyId().equals(request.partyId())) {
            throw new ResourceNotFoundException("Beneficiary not found");
        }
        if (beneficiary.getStatus() != BeneficiaryStatus.ACTIVE) {
            throw new BadRequestException("Beneficiary is not active");
        }

        Instant now = Instant.now();
        String txnId = UUID.randomUUID().toString();

        FinancialTransactionEntity txn = new FinancialTransactionEntity();
        txn.setId(txnId);
        txn.setTenantId(tenantId);
        txn.setPartyId(request.partyId());
        txn.setAccountId(request.accountId());
        txn.setTxnType(TransactionType.PAYMENT);
        txn.setStatus(TransactionStatus.COMPLETED);
        txn.setAmount(request.amount());
        txn.setCurrency(request.currency());
        txn.setReference(request.reference());
        txn.setDescription("Payment to " + beneficiary.getNickname());
        txn.setIdempotencyKey(idempotencyKey);
        txn.setCreatedAt(now);
        txn.setUpdatedAt(now);
        transactionRepository.save(txn);

        ledgerService.debit(tenantId, request.accountId(), request.amount(), request.currency(), txnId);

        PaymentEntity payment = new PaymentEntity();
        payment.setId(UUID.randomUUID().toString());
        payment.setTenantId(tenantId);
        payment.setTransactionId(txnId);
        payment.setPartyId(request.partyId());
        payment.setAccountId(request.accountId());
        payment.setBeneficiaryId(request.beneficiaryId());
        payment.setAmount(request.amount());
        payment.setCurrency(request.currency());
        payment.setReference(request.reference());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setCreatedAt(now);
        paymentRepository.save(payment);

        auditService.record(tenantId, "PAYMENT", payment.getId(), "CREATED", actorId,
                "amount=" + request.amount() + " " + request.currency());

        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(String tenantId, String paymentId) {
        PaymentEntity payment = paymentRepository.findByIdAndTenantId(paymentId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> listPayments(
            String tenantId, String partyId, PaymentStatus status, int page, int size) {
        partyService.requirePartyInTenant(tenantId, partyId);
        Page<PaymentEntity> result = status == null
                ? paymentRepository.findByTenantIdAndPartyId(tenantId, partyId, PageRequest.of(page, size))
                : paymentRepository.findByTenantIdAndPartyIdAndStatus(tenantId, partyId, status, PageRequest.of(page, size));
        return new PageResponse<>(
                result.getContent().stream().map(PaymentService::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), null);
    }

    static PaymentResponse toResponse(PaymentEntity payment) {
        return new PaymentResponse(
                payment.getId(), payment.getTransactionId(), payment.getPartyId(),
                payment.getAccountId(), payment.getBeneficiaryId(), payment.getAmount(),
                payment.getCurrency(), payment.getReference(), payment.getStatus(), payment.getCreatedAt());
    }
}
