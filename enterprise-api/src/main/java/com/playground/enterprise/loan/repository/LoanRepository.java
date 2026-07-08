package com.playground.enterprise.loan.repository;

import com.playground.enterprise.domain.LoanStatus;
import com.playground.enterprise.loan.entity.LoanEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanRepository extends JpaRepository<LoanEntity, String> {

    Optional<LoanEntity> findByIdAndTenantId(String id, String tenantId);

    Page<LoanEntity> findByTenantIdAndPartyId(String tenantId, String partyId, Pageable pageable);

    Page<LoanEntity> findByTenantIdAndPartyIdAndStatus(
            String tenantId, String partyId, LoanStatus status, Pageable pageable);
}
