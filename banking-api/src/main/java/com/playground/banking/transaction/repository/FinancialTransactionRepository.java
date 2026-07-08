package com.playground.banking.transaction.repository;

import com.playground.banking.domain.TransactionStatus;
import com.playground.banking.domain.TransactionType;
import com.playground.banking.transaction.entity.FinancialTransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface FinancialTransactionRepository extends JpaRepository<FinancialTransactionEntity, String> {

    Optional<FinancialTransactionEntity> findByIdAndTenantId(String id, String tenantId);

    Page<FinancialTransactionEntity> findByTenantIdAndAccountId(String tenantId, String accountId, Pageable pageable);

    Page<FinancialTransactionEntity> findByTenantIdAndAccountIdAndTxnType(
            String tenantId, String accountId, TransactionType txnType, Pageable pageable);

    Page<FinancialTransactionEntity> findByTenantIdAndAccountIdAndStatus(
            String tenantId, String accountId, TransactionStatus status, Pageable pageable);

    Page<FinancialTransactionEntity> findByTenantIdAndAccountIdAndCreatedAtBetween(
            String tenantId, String accountId, Instant from, Instant to, Pageable pageable);

    Page<FinancialTransactionEntity> findByTenantIdAndAccountIdAndTxnTypeAndStatusAndCreatedAtBetween(
            String tenantId,
            String accountId,
            TransactionType txnType,
            TransactionStatus status,
            Instant from,
            Instant to,
            Pageable pageable);

    @Query("""
            SELECT t FROM FinancialTransactionEntity t
            WHERE t.tenantId = :tenantId AND t.accountId = :accountId
            AND (:txnType IS NULL OR t.txnType = :txnType)
            AND (:status IS NULL OR t.status = :status)
            AND (:from IS NULL OR t.createdAt >= :from)
            AND (:to IS NULL OR t.createdAt <= :to)
            ORDER BY t.createdAt DESC
            """)
    Page<FinancialTransactionEntity> search(
            @Param("tenantId") String tenantId,
            @Param("accountId") String accountId,
            @Param("txnType") TransactionType txnType,
            @Param("status") TransactionStatus status,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable);
}
