package com.playground.banking.transfer.repository;

import com.playground.banking.transfer.entity.TransferEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransferRepository extends JpaRepository<TransferEntity, String> {

    Optional<TransferEntity> findByIdAndTenantId(String id, String tenantId);

    Page<TransferEntity> findByTenantIdAndFromAccountId(String tenantId, String fromAccountId, Pageable pageable);
}
