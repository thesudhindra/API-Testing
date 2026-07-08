package com.playground.enterprise.admin.dto;

import java.time.Instant;

public record TenantSettingResponse(
        String settingKey,
        String settingValue,
        String updatedBy,
        Instant updatedAt
) {
}
