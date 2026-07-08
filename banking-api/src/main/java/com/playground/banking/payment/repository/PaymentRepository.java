package com.playground.banking.payment.repository;

import com.playground.banking.domain.PaymentStatus;
import com.playground.banking.payment.entity.PaymentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {

    Optional<PaymentEntity> findByIdAndTenantId(String id, String tenantId);

    Page<PaymentEntity> findByTenantIdAndPartyId(String tenantId, String partyId, Pageable pageable);

    Page<PaymentEntity> findByTenantIdAndPartyIdAndStatus(
            String tenantId, String partyId, PaymentStatus status, Pageable pageable);
}
