package com.playground.enterprise.scheduler.repository;

import com.playground.enterprise.scheduler.entity.ScheduledTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ScheduledTaskRepository extends JpaRepository<ScheduledTaskEntity, String> {

    Optional<ScheduledTaskEntity> findByIdAndTenantId(String id, String tenantId);

    List<ScheduledTaskEntity> findByEnabledTrueAndNextRunAtBefore(Instant nextRunAt);
}
