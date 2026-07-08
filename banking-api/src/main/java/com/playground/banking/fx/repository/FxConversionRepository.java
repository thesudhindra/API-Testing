package com.playground.banking.fx.repository;

import com.playground.banking.fx.entity.FxConversionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FxConversionRepository extends JpaRepository<FxConversionEntity, String> {

    Optional<FxConversionEntity> findByIdAndTenantId(String id, String tenantId);
}
