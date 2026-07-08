package com.playground.banking.audit.repository;

import com.playground.banking.audit.entity.AuditEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEventEntity, String> {
}
