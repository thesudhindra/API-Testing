package com.playground.enterprise.notification.repository;

import com.playground.enterprise.domain.NotificationStatus;
import com.playground.enterprise.notification.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<NotificationEntity, String> {

    Optional<NotificationEntity> findByIdAndTenantId(String id, String tenantId);

    Page<NotificationEntity> findByTenantIdAndPartyId(String tenantId, String partyId, Pageable pageable);

    Page<NotificationEntity> findByTenantIdAndPartyIdAndStatus(
            String tenantId, String partyId, NotificationStatus status, Pageable pageable);
}
