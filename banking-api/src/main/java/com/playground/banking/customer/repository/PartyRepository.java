package com.playground.banking.customer.repository;

import com.playground.banking.customer.entity.PartyEntity;
import com.playground.banking.domain.PartyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartyRepository extends JpaRepository<PartyEntity, String> {

    Page<PartyEntity> findByTenantId(String tenantId, Pageable pageable);

    Page<PartyEntity> findByTenantIdAndStatus(String tenantId, PartyStatus status, Pageable pageable);

    Optional<PartyEntity> findByIdAndTenantId(String id, String tenantId);
}
