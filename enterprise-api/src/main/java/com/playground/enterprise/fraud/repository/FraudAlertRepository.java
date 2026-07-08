package com.playground.enterprise.fraud.repository;

import com.playground.enterprise.domain.FraudAlertStatus;
import com.playground.enterprise.fraud.entity.FraudAlertEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FraudAlertRepository extends JpaRepository<FraudAlertEntity, String> {

    Optional<FraudAlertEntity> findByIdAndTenantId(String id, String tenantId);

    Page<FraudAlertEntity> findByTenantId(String tenantId, Pageable pageable);

    Page<FraudAlertEntity> findByTenantIdAndPartyId(String tenantId, String partyId, Pageable pageable);

    Page<FraudAlertEntity> findByTenantIdAndStatus(String tenantId, FraudAlertStatus status, Pageable pageable);

    Page<FraudAlertEntity> findByTenantIdAndPartyIdAndStatus(
            String tenantId, String partyId, FraudAlertStatus status, Pageable pageable);
}
