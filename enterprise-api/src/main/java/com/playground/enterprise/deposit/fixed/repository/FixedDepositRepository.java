package com.playground.enterprise.deposit.fixed.repository;

import com.playground.enterprise.deposit.fixed.entity.FixedDepositEntity;
import com.playground.enterprise.domain.DepositStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FixedDepositRepository extends JpaRepository<FixedDepositEntity, String> {

    Optional<FixedDepositEntity> findByIdAndTenantId(String id, String tenantId);

    Page<FixedDepositEntity> findByTenantIdAndPartyId(String tenantId, String partyId, Pageable pageable);

    Page<FixedDepositEntity> findByTenantIdAndPartyIdAndStatus(
            String tenantId, String partyId, DepositStatus status, Pageable pageable);
}
