package com.playground.enterprise.admin.repository;

import com.playground.enterprise.admin.entity.TenantSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TenantSettingRepository extends JpaRepository<TenantSettingEntity, TenantSettingEntity.Key> {

    List<TenantSettingEntity> findByTenantId(String tenantId);

    Optional<TenantSettingEntity> findByTenantIdAndSettingKey(String tenantId, String settingKey);
}
