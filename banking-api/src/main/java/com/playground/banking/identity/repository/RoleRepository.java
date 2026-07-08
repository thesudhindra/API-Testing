package com.playground.banking.identity.repository;

import com.playground.banking.identity.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, String> {

    List<RoleEntity> findByTenantId(String tenantId);

    Optional<RoleEntity> findByTenantIdAndName(String tenantId, String name);
}
