package com.playground.banking.card.service;

import com.playground.banking.account.repository.AccountRepository;
import com.playground.banking.audit.service.AuditService;
import com.playground.banking.customer.service.PartyService;
import com.playground.banking.domain.CardAuthorizationStatus;
import com.playground.banking.domain.CardStatus;
import com.playground.banking.domain.TransactionStatus;
import com.playground.banking.domain.TransactionType;
import com.playground.banking.card.dto.AuthorizeCardRequest;
import com.playground.banking.card.dto.CardAuthorizationResponse;
import com.playground.banking.card.dto.CardResponse;
import com.playground.banking.card.dto.CreateCardRequest;
import com.playground.banking.card.entity.CardAuthorizationEntity;
import com.playground.banking.card.entity.CardEntity;
import com.playground.banking.card.repository.CardAuthorizationRepository;
import com.playground.banking.card.repository.CardRepository;
import com.playground.banking.ledger.service.LedgerService;
import com.playground.banking.transaction.entity.FinancialTransactionEntity;
import com.playground.banking.transaction.repository.FinancialTransactionRepository;
import com.playground.common.exception.BadRequestException;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final CardAuthorizationRepository cardAuthorizationRepository;
    private final AccountRepository accountRepository;
    private final PartyService partyService;
    private final FinancialTransactionRepository transactionRepository;
    private final LedgerService ledgerService;
    private final AuditService auditService;

    public CardService(
            CardRepository cardRepository,
            CardAuthorizationRepository cardAuthorizationRepository,
            AccountRepository accountRepository,
            PartyService partyService,
            FinancialTransactionRepository transactionRepository,
            LedgerService ledgerService,
            AuditService auditService) {
        this.cardRepository = cardRepository;
        this.cardAuthorizationRepository = cardAuthorizationRepository;
        this.accountRepository = accountRepository;
        this.partyService = partyService;
        this.transactionRepository = transactionRepository;
        this.ledgerService = ledgerService;
        this.auditService = auditService;
    }

    @Transactional
    public CardResponse createCard(String tenantId, String actorId, CreateCardRequest request) {
        partyService.requirePartyInTenant(tenantId, request.partyId());
        accountRepository.findByIdAndTenantId(request.accountId(), tenantId)
                .filter(a -> a.getPartyId().equals(request.partyId()))
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        CardEntity card = new CardEntity();
        card.setId(UUID.randomUUID().toString());
        card.setTenantId(tenantId);
        card.setPartyId(request.partyId());
        card.setAccountId(request.accountId());
        card.setPanLast4(String.format("%04d", ThreadLocalRandom.current().nextInt(0, 10000)));
        card.setProductCode(request.productCode());
        card.setStatus(CardStatus.ACTIVE);
        card.setCreatedAt(Instant.now());
        cardRepository.save(card);

        auditService.record(tenantId, "CARD", card.getId(), "ISSUED", actorId, "account=" + request.accountId());
        return toResponse(card);
    }

    @Transactional(readOnly = true)
    public List<CardResponse> listCards(String tenantId, String partyId) {
        partyService.requirePartyInTenant(tenantId, partyId);
        return cardRepository.findByTenantIdAndPartyIdAndStatusNot(tenantId, partyId, CardStatus.CANCELLED).stream()
                .map(CardService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CardResponse getCard(String tenantId, String cardId) {
        CardEntity card = cardRepository.findByIdAndTenantId(cardId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        return toResponse(card);
    }

    @Transactional
    public CardAuthorizationResponse authorize(
            String tenantId, String actorId, String cardId, String idempotencyKey, AuthorizeCardRequest request) {
        CardEntity card = cardRepository.findByIdAndTenantId(cardId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new BadRequestException("Card is not active");
        }

        Instant now = Instant.now();
        String txnId = UUID.randomUUID().toString();

        FinancialTransactionEntity txn = new FinancialTransactionEntity();
        txn.setId(txnId);
        txn.setTenantId(tenantId);
        txn.setPartyId(card.getPartyId());
        txn.setAccountId(card.getAccountId());
        txn.setTxnType(TransactionType.CARD_AUTHORIZATION);
        txn.setStatus(TransactionStatus.COMPLETED);
        txn.setAmount(request.amount());
        txn.setCurrency(request.currency());
        txn.setDescription("Card purchase at " + request.merchantName());
        txn.setIdempotencyKey(idempotencyKey);
        txn.setCreatedAt(now);
        txn.setUpdatedAt(now);
        transactionRepository.save(txn);

        ledgerService.debit(tenantId, card.getAccountId(), request.amount(), request.currency(), txnId);

        CardAuthorizationEntity auth = new CardAuthorizationEntity();
        auth.setId(UUID.randomUUID().toString());
        auth.setTenantId(tenantId);
        auth.setCardId(cardId);
        auth.setTransactionId(txnId);
        auth.setMerchantName(request.merchantName());
        auth.setAmount(request.amount());
        auth.setCurrency(request.currency());
        auth.setStatus(CardAuthorizationStatus.APPROVED);
        auth.setCreatedAt(now);
        cardAuthorizationRepository.save(auth);

        auditService.record(tenantId, "CARD_AUTH", auth.getId(), "APPROVED", actorId,
                "merchant=" + request.merchantName());

        return new CardAuthorizationResponse(
                auth.getId(), cardId, txnId, request.merchantName(),
                request.amount(), request.currency(), auth.getStatus().name(), now);
    }

    static CardResponse toResponse(CardEntity card) {
        return new CardResponse(
                card.getId(), card.getPartyId(), card.getAccountId(),
                card.getPanLast4(), card.getProductCode(), card.getStatus(), card.getCreatedAt());
    }
}
