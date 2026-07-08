package com.playground.banking.statement.repository;

import com.playground.banking.statement.entity.StatementLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatementLineRepository extends JpaRepository<StatementLineEntity, String> {

    List<StatementLineEntity> findByStatementIdOrderBySortOrderAsc(String statementId);
}
