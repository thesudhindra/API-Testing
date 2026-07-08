package com.playground.enterprise.aml.repository;

import com.playground.enterprise.aml.entity.AmlCaseEntity;
import com.playground.enterprise.domain.AmlCaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AmlCaseRepository extends JpaRepository<AmlCaseEntity, String> {

    Optional<AmlCaseEntity> findByIdAndTenantId(String id, String tenantId);

    Page<AmlCaseEntity> findByTenantIdAndPartyId(String tenantId, String partyId, Pageable pageable);

    Page<AmlCaseEntity> findByTenantIdAndPartyIdAndStatus(
            String tenantId, String partyId, AmlCaseStatus status, Pageable pageable);
}
