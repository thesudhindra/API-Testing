package com.playground.enterprise.audit.repository;

import com.playground.enterprise.audit.entity.AuditEventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface AuditEventRepository extends JpaRepository<AuditEventEntity, String> {

    Optional<AuditEventEntity> findByIdAndTenantId(String id, String tenantId);

    Optional<AuditEventEntity> findTopByTenantIdOrderByCreatedAtDesc(String tenantId);

    @Query("""
            SELECT e FROM AuditEventEntity e
            WHERE e.tenantId = :tenantId
            AND (:entityType IS NULL OR e.entityType = :entityType)
            AND (:correlationId IS NULL OR e.correlationId = :correlationId)
            AND e.createdAt >= :from AND e.createdAt <= :to
            """)
    Page<AuditEventEntity> search(
            @Param("tenantId") String tenantId,
            @Param("entityType") String entityType,
            @Param("correlationId") String correlationId,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable);
}
