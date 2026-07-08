package com.playground.banking.ledger.repository;

import com.playground.banking.ledger.entity.LedgerEntryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntryEntity, String> {

    Page<LedgerEntryEntity> findByTenantIdAndAccountId(String tenantId, String accountId, Pageable pageable);

    Page<LedgerEntryEntity> findByTenantIdAndAccountIdAndCreatedAtBetween(
            String tenantId, String accountId, Instant from, Instant to, Pageable pageable);

    Optional<LedgerEntryEntity> findTopByTenantIdAndAccountIdAndCreatedAtBeforeOrderByCreatedAtDesc(
            String tenantId, String accountId, Instant before);
}
