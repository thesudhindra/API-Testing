package com.playground.enterprise.event.repository;

import com.playground.enterprise.domain.EventStatus;
import com.playground.enterprise.event.entity.DomainEventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DomainEventRepository extends JpaRepository<DomainEventEntity, String> {

    Optional<DomainEventEntity> findByIdAndTenantId(String id, String tenantId);

    List<DomainEventEntity> findByStatusOrderByCreatedAtAsc(EventStatus status);

    Page<DomainEventEntity> findByTenantIdAndStatus(
            String tenantId, EventStatus status, Pageable pageable);
}
