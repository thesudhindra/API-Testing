package com.playground.enterprise.loan.service;

import com.playground.enterprise.audit.service.EnterpriseAuditService;
import com.playground.enterprise.domain.LoanStatus;
import com.playground.enterprise.domain.RepaymentStatus;
import com.playground.enterprise.event.service.EventOutboxService;
import com.playground.enterprise.loan.dto.CreateLoanRequest;
import com.playground.enterprise.loan.dto.CreateRepaymentRequest;
import com.playground.enterprise.loan.dto.LoanResponse;
import com.playground.enterprise.loan.dto.RepaymentResponse;
import com.playground.enterprise.loan.entity.LoanEntity;
import com.playground.enterprise.loan.entity.LoanRepaymentEntity;
import com.playground.enterprise.loan.repository.LoanRepaymentRepository;
import com.playground.enterprise.loan.repository.LoanRepository;
import com.playground.common.dto.PageResponse;
import com.playground.common.exception.BadRequestException;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final LoanRepaymentRepository loanRepaymentRepository;
    private final EnterpriseAuditService auditService;
    private final EventOutboxService eventOutboxService;

    public LoanService(
            LoanRepository loanRepository,
            LoanRepaymentRepository loanRepaymentRepository,
            EnterpriseAuditService auditService,
            EventOutboxService eventOutboxService) {
        this.loanRepository = loanRepository;
        this.loanRepaymentRepository = loanRepaymentRepository;
        this.auditService = auditService;
        this.eventOutboxService = eventOutboxService;
    }

    @Transactional
    public LoanResponse createLoan(String tenantId, String actorId, CreateLoanRequest request) {
        Instant now = Instant.now();
        String loanId = UUID.randomUUID().toString();

        LoanEntity loan = new LoanEntity();
        loan.setId(loanId);
        loan.setTenantId(tenantId);
        loan.setPartyId(request.partyId());
        loan.setAccountId(request.accountId());
        loan.setProductCode(request.productCode());
        loan.setPrincipal(request.principal());
        loan.setCurrency(request.currency());
        loan.setInterestRate(request.interestRate());
        loan.setTermMonths(request.termMonths());
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setOutstandingBalance(request.principal());
        loan.setCreatedAt(now);
        loan.setUpdatedAt(now);
        loanRepository.save(loan);

        auditService.record(tenantId, "LOAN", loanId, "CREATED", actorId,
                "principal=" + request.principal() + " " + request.currency());
        eventOutboxService.publish(tenantId, "LOAN", loanId, "LOAN_CREATED",
                "partyId=" + request.partyId() + ",principal=" + request.principal());

        return toResponse(loan);
    }

    @Transactional(readOnly = true)
    public LoanResponse getLoan(String tenantId, String loanId) {
        LoanEntity loan = loanRepository.findByIdAndTenantId(loanId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        return toResponse(loan);
    }

    @Transactional(readOnly = true)
    public PageResponse<LoanResponse> listLoans(
            String tenantId, String partyId, LoanStatus status, int page, int size) {
        Page<LoanEntity> result = status == null
                ? loanRepository.findByTenantIdAndPartyId(tenantId, partyId, PageRequest.of(page, size))
                : loanRepository.findByTenantIdAndPartyIdAndStatus(tenantId, partyId, status, PageRequest.of(page, size));
        return new PageResponse<>(
                result.getContent().stream().map(LoanService::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), null);
    }

    @Transactional
    public RepaymentResponse repayLoan(
            String tenantId, String actorId, String loanId, CreateRepaymentRequest request) {
        LoanEntity loan = loanRepository.findByIdAndTenantId(loanId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new BadRequestException("Loan is not active");
        }
        if (request.amount().compareTo(loan.getOutstandingBalance()) > 0) {
            throw new BadRequestException("Repayment amount exceeds outstanding balance");
        }

        Instant now = Instant.now();
        String repaymentId = UUID.randomUUID().toString();

        LoanRepaymentEntity repayment = new LoanRepaymentEntity();
        repayment.setId(repaymentId);
        repayment.setTenantId(tenantId);
        repayment.setLoanId(loanId);
        repayment.setAmount(request.amount());
        repayment.setCurrency(loan.getCurrency());
        repayment.setStatus(RepaymentStatus.COMPLETED);
        repayment.setCreatedAt(now);
        loanRepaymentRepository.save(repayment);

        BigDecimal newBalance = loan.getOutstandingBalance().subtract(request.amount());
        loan.setOutstandingBalance(newBalance);
        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(LoanStatus.CLOSED);
        }
        loan.setUpdatedAt(now);
        loanRepository.save(loan);

        auditService.record(tenantId, "LOAN_REPAYMENT", repaymentId, "CREATED", actorId,
                "loanId=" + loanId + ",amount=" + request.amount());
        eventOutboxService.publish(tenantId, "LOAN", loanId, "LOAN_REPAID",
                "repaymentId=" + repaymentId + ",amount=" + request.amount());

        return toResponse(repayment);
    }

    static LoanResponse toResponse(LoanEntity loan) {
        return new LoanResponse(
                loan.getId(), loan.getPartyId(), loan.getAccountId(), loan.getProductCode(),
                loan.getPrincipal(), loan.getCurrency(), loan.getInterestRate(), loan.getTermMonths(),
                loan.getStatus(), loan.getOutstandingBalance(), loan.getCreatedAt(), loan.getUpdatedAt());
    }

    static RepaymentResponse toResponse(LoanRepaymentEntity repayment) {
        return new RepaymentResponse(
                repayment.getId(), repayment.getLoanId(), repayment.getAmount(),
                repayment.getCurrency(), repayment.getStatus(), repayment.getCreatedAt());
    }
}
