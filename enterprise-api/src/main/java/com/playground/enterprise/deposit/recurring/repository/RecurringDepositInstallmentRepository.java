package com.playground.enterprise.deposit.recurring.repository;

import com.playground.enterprise.deposit.recurring.entity.RecurringDepositInstallmentEntity;
import com.playground.enterprise.domain.InstallmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecurringDepositInstallmentRepository extends JpaRepository<RecurringDepositInstallmentEntity, String> {

    Optional<RecurringDepositInstallmentEntity> findByIdAndTenantId(String id, String tenantId);

    Page<RecurringDepositInstallmentEntity> findByTenantIdAndRecurringDepositId(
            String tenantId, String recurringDepositId, Pageable pageable);

    Page<RecurringDepositInstallmentEntity> findByTenantIdAndRecurringDepositIdAndStatus(
            String tenantId, String recurringDepositId, InstallmentStatus status, Pageable pageable);
}
