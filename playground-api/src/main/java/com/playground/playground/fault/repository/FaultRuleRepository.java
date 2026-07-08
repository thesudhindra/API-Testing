package com.playground.playground.fault.repository;

import com.playground.playground.domain.FaultTargetService;
import com.playground.playground.fault.entity.FaultRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface FaultRuleRepository extends JpaRepository<FaultRuleEntity, String> {

    List<FaultRuleEntity> findByEnabledTrue();

    List<FaultRuleEntity> findByTargetServiceAndEnabledTrue(FaultTargetService targetService);

    List<FaultRuleEntity> findByEnabledTrueAndExpiresAtBefore(Instant expiresAt);
}
