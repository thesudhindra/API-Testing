package com.playground.enterprise.admin.service;

import com.playground.enterprise.admin.dto.TenantSettingResponse;
import com.playground.enterprise.admin.dto.UpdateSettingRequest;
import com.playground.enterprise.admin.entity.TenantSettingEntity;
import com.playground.enterprise.admin.repository.TenantSettingRepository;
import com.playground.enterprise.audit.service.EnterpriseAuditService;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class AdminService {

    private final TenantSettingRepository tenantSettingRepository;
    private final EnterpriseAuditService auditService;

    public AdminService(
            TenantSettingRepository tenantSettingRepository,
            EnterpriseAuditService auditService) {
        this.tenantSettingRepository = tenantSettingRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<TenantSettingResponse> listSettings(String tenantId) {
        return tenantSettingRepository.findByTenantId(tenantId).stream()
                .map(AdminService::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TenantSettingResponse getSetting(String tenantId, String key) {
        TenantSettingEntity setting = tenantSettingRepository.findByTenantIdAndSettingKey(tenantId, key)
                .orElseThrow(() -> new ResourceNotFoundException("Setting not found"));
        return toResponse(setting);
    }

    @Transactional
    public TenantSettingResponse upsertSetting(
            String tenantId, String actorId, String key, UpdateSettingRequest request) {
        Instant now = Instant.now();

        TenantSettingEntity setting = tenantSettingRepository
                .findByTenantIdAndSettingKey(tenantId, key)
                .orElseGet(() -> {
                    TenantSettingEntity entity = new TenantSettingEntity();
                    entity.setTenantId(tenantId);
                    entity.setSettingKey(key);
                    return entity;
                });

        setting.setSettingValue(request.settingValue());
        setting.setUpdatedBy(actorId);
        setting.setUpdatedAt(now);
        tenantSettingRepository.save(setting);

        auditService.record(tenantId, "TENANT_SETTING", key, "UPDATED", actorId,
                "value=" + request.settingValue());

        return toResponse(setting);
    }

    static TenantSettingResponse toResponse(TenantSettingEntity setting) {
        return new TenantSettingResponse(
                setting.getSettingKey(), setting.getSettingValue(),
                setting.getUpdatedBy(), setting.getUpdatedAt());
    }
}
