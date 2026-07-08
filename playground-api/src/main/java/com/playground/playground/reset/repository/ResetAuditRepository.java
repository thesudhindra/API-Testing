package com.playground.playground.reset.repository;

import com.playground.playground.reset.entity.ResetAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResetAuditRepository extends JpaRepository<ResetAuditEntity, String> {

    List<ResetAuditEntity> findTop10ByOrderByCreatedAtDesc();
}
