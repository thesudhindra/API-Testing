package com.playground.banking.fx.service;

import com.playground.banking.account.entity.AccountEntity;
import com.playground.banking.account.repository.AccountRepository;
import com.playground.banking.audit.service.AuditService;
import com.playground.banking.customer.service.PartyService;
import com.playground.banking.domain.TransactionStatus;
import com.playground.banking.domain.TransactionType;
import com.playground.banking.fx.dto.CreateFxQuoteRequest;
import com.playground.banking.fx.dto.ExecuteFxConversionRequest;
import com.playground.banking.fx.dto.FxConversionResponse;
import com.playground.banking.fx.dto.FxQuoteResponse;
import com.playground.banking.fx.entity.FxConversionEntity;
import com.playground.banking.fx.entity.FxQuoteEntity;
import com.playground.banking.fx.repository.FxConversionRepository;
import com.playground.banking.fx.repository.FxQuoteRepository;
import com.playground.banking.ledger.service.LedgerService;
import com.playground.banking.transaction.entity.FinancialTransactionEntity;
import com.playground.banking.transaction.repository.FinancialTransactionRepository;
import com.playground.common.exception.BadRequestException;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class FxService {

    private static final Map<String, BigDecimal> RATES = Map.of(
            "GBP_EUR", new BigDecimal("1.17000000"),
            "EUR_GBP", new BigDecimal("0.85000000"),
            "GBP_USD", new BigDecimal("1.27000000"),
            "USD_GBP", new BigDecimal("0.79000000"));

    private final FxQuoteRepository fxQuoteRepository;
    private final FxConversionRepository fxConversionRepository;
    private final AccountRepository accountRepository;
    private final PartyService partyService;
    private final FinancialTransactionRepository transactionRepository;
    private final LedgerService ledgerService;
    private final AuditService auditService;

    public FxService(
            FxQuoteRepository fxQuoteRepository,
            FxConversionRepository fxConversionRepository,
            AccountRepository accountRepository,
            PartyService partyService,
            FinancialTransactionRepository transactionRepository,
            LedgerService ledgerService,
            AuditService auditService) {
        this.fxQuoteRepository = fxQuoteRepository;
        this.fxConversionRepository = fxConversionRepository;
        this.accountRepository = accountRepository;
        this.partyService = partyService;
        this.transactionRepository = transactionRepository;
        this.ledgerService = ledgerService;
        this.auditService = auditService;
    }

    @Transactional
    public FxQuoteResponse createQuote(String tenantId, CreateFxQuoteRequest request) {
        if (request.fromCurrency().equals(request.toCurrency())) {
            throw new BadRequestException("fromCurrency and toCurrency must differ");
        }
        BigDecimal rate = resolveRate(request.fromCurrency(), request.toCurrency());
        BigDecimal toAmount = request.fromAmount().multiply(rate).setScale(4, RoundingMode.HALF_UP);

        Instant now = Instant.now();
        FxQuoteEntity quote = new FxQuoteEntity();
        quote.setId(UUID.randomUUID().toString());
        quote.setTenantId(tenantId);
        quote.setFromCurrency(request.fromCurrency());
        quote.setToCurrency(request.toCurrency());
        quote.setRate(rate);
        quote.setFromAmount(request.fromAmount());
        quote.setToAmount(toAmount);
        quote.setExpiresAt(now.plusSeconds(300));
        quote.setCreatedAt(now);
        fxQuoteRepository.save(quote);
        return toQuoteResponse(quote);
    }

    @Transactional
    public FxConversionResponse executeConversion(
            String tenantId, String actorId, String idempotencyKey, ExecuteFxConversionRequest request) {
        partyService.requirePartyInTenant(tenantId, request.partyId());

        FxQuoteEntity quote = fxQuoteRepository.findByIdAndTenantId(request.quoteId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("FX quote not found"));
        if (quote.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("FX quote has expired");
        }

        AccountEntity from = accountRepository.findByIdAndTenantId(request.fromAccountId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found"));
        AccountEntity to = accountRepository.findByIdAndTenantId(request.toAccountId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Destination account not found"));

        if (!from.getCurrency().equals(quote.getFromCurrency()) || !to.getCurrency().equals(quote.getToCurrency())) {
            throw new BadRequestException("Account currencies do not match quote");
        }
        if (!from.getPartyId().equals(request.partyId())) {
            throw new BadRequestException("Source account does not belong to party");
        }

        Instant now = Instant.now();
        String txnId = UUID.randomUUID().toString();

        FinancialTransactionEntity txn = new FinancialTransactionEntity();
        txn.setId(txnId);
        txn.setTenantId(tenantId);
        txn.setPartyId(request.partyId());
        txn.setAccountId(from.getId());
        txn.setTxnType(TransactionType.FX_CONVERSION);
        txn.setStatus(TransactionStatus.COMPLETED);
        txn.setAmount(quote.getFromAmount());
        txn.setCurrency(quote.getFromCurrency());
        txn.setDescription("FX " + quote.getFromCurrency() + " to " + quote.getToCurrency());
        txn.setIdempotencyKey(idempotencyKey);
        txn.setCreatedAt(now);
        txn.setUpdatedAt(now);
        transactionRepository.save(txn);

        ledgerService.debit(tenantId, from.getId(), quote.getFromAmount(), quote.getFromCurrency(), txnId);
        ledgerService.credit(tenantId, to.getId(), quote.getToAmount(), quote.getToCurrency(), txnId);

        FxConversionEntity conversion = new FxConversionEntity();
        conversion.setId(UUID.randomUUID().toString());
        conversion.setTenantId(tenantId);
        conversion.setTransactionId(txnId);
        conversion.setQuoteId(quote.getId());
        conversion.setFromAccountId(from.getId());
        conversion.setToAccountId(to.getId());
        conversion.setFromAmount(quote.getFromAmount());
        conversion.setToAmount(quote.getToAmount());
        conversion.setFromCurrency(quote.getFromCurrency());
        conversion.setToCurrency(quote.getToCurrency());
        conversion.setRate(quote.getRate());
        conversion.setCreatedAt(now);
        fxConversionRepository.save(conversion);

        auditService.record(tenantId, "FX_CONVERSION", conversion.getId(), "EXECUTED", actorId,
                "rate=" + quote.getRate());

        return toConversionResponse(conversion);
    }

    @Transactional(readOnly = true)
    public FxConversionResponse getConversion(String tenantId, String conversionId) {
        FxConversionEntity conversion = fxConversionRepository.findByIdAndTenantId(conversionId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("FX conversion not found"));
        return toConversionResponse(conversion);
    }

    private static BigDecimal resolveRate(String from, String to) {
        BigDecimal rate = RATES.get(from + "_" + to);
        if (rate == null) {
            throw new BadRequestException("FX rate not available for currency pair");
        }
        return rate;
    }

    static FxQuoteResponse toQuoteResponse(FxQuoteEntity quote) {
        return new FxQuoteResponse(
                quote.getId(), quote.getFromCurrency(), quote.getToCurrency(), quote.getRate(),
                quote.getFromAmount(), quote.getToAmount(), quote.getExpiresAt(), quote.getCreatedAt());
    }

    static FxConversionResponse toConversionResponse(FxConversionEntity conversion) {
        return new FxConversionResponse(
                conversion.getId(), conversion.getTransactionId(), conversion.getQuoteId(),
                conversion.getFromAccountId(), conversion.getToAccountId(),
                conversion.getFromAmount(), conversion.getToAmount(),
                conversion.getFromCurrency(), conversion.getToCurrency(),
                conversion.getRate(), conversion.getCreatedAt());
    }
}
