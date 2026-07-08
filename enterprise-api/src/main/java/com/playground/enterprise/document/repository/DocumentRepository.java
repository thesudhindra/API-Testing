package com.playground.enterprise.document.repository;

import com.playground.enterprise.document.entity.DocumentEntity;
import com.playground.enterprise.domain.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentRepository extends JpaRepository<DocumentEntity, String> {

    Optional<DocumentEntity> findByIdAndTenantId(String id, String tenantId);

    Page<DocumentEntity> findByTenantIdAndPartyId(String tenantId, String partyId, Pageable pageable);

    Page<DocumentEntity> findByTenantIdAndPartyIdAndStatus(
            String tenantId, String partyId, DocumentStatus status, Pageable pageable);
}
