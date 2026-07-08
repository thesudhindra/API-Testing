package com.playground.banking.fx.repository;

import com.playground.banking.fx.entity.FxConversionEntity;
import com.playground.banking.fx.entity.FxQuoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FxQuoteRepository extends JpaRepository<FxQuoteEntity, String> {

    Optional<FxQuoteEntity> findByIdAndTenantId(String id, String tenantId);
}
