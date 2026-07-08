package com.playground.banking.statement.repository;

import com.playground.banking.statement.entity.StatementEntity;
import com.playground.banking.statement.entity.StatementLineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatementRepository extends JpaRepository<StatementEntity, String> {

    Page<StatementEntity> findByTenantIdAndAccountId(String tenantId, String accountId, Pageable pageable);

    Optional<StatementEntity> findByIdAndTenantId(String id, String tenantId);
}
