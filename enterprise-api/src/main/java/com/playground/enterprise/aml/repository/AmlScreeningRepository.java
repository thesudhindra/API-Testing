package com.playground.enterprise.aml.repository;

import com.playground.enterprise.aml.entity.AmlScreeningEntity;
import com.playground.enterprise.domain.ScreeningResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AmlScreeningRepository extends JpaRepository<AmlScreeningEntity, String> {

    Optional<AmlScreeningEntity> findByIdAndTenantId(String id, String tenantId);

    Page<AmlScreeningEntity> findByTenantIdAndPartyId(String tenantId, String partyId, Pageable pageable);

    Page<AmlScreeningEntity> findByTenantIdAndPartyIdAndResult(
            String tenantId, String partyId, ScreeningResult result, Pageable pageable);
}
