package com.playground.enterprise.deposit.recurring.repository;

import com.playground.enterprise.deposit.recurring.entity.RecurringDepositEntity;
import com.playground.enterprise.domain.DepositStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecurringDepositRepository extends JpaRepository<RecurringDepositEntity, String> {

    Optional<RecurringDepositEntity> findByIdAndTenantId(String id, String tenantId);

    Page<RecurringDepositEntity> findByTenantIdAndPartyId(String tenantId, String partyId, Pageable pageable);

    Page<RecurringDepositEntity> findByTenantIdAndPartyIdAndStatus(
            String tenantId, String partyId, DepositStatus status, Pageable pageable);
}
