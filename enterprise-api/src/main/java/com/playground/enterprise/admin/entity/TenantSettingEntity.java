package com.playground.enterprise.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(schema = "enterprise", name = "tenant_settings")
@IdClass(TenantSettingEntity.Key.class)
public class TenantSettingEntity {

    @Id
    @Column(name = "tenant_id", length = 36)
    private String tenantId;

    @Id
    @Column(name = "setting_key", length = 64)
    private String settingKey;

    @Column(name = "setting_value", nullable = false, length = 1024)
    private String settingValue;

    @Column(name = "updated_by", length = 36)
    private String updatedBy;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getSettingKey() { return settingKey; }
    public void setSettingKey(String settingKey) { this.settingKey = settingKey; }
    public String getSettingValue() { return settingValue; }
    public void setSettingValue(String settingValue) { this.settingValue = settingValue; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static class Key implements Serializable {
        private String tenantId;
        private String settingKey;

        public Key() {}

        public Key(String tenantId, String settingKey) {
            this.tenantId = tenantId;
            this.settingKey = settingKey;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key key)) return false;
            return Objects.equals(tenantId, key.tenantId) && Objects.equals(settingKey, key.settingKey);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tenantId, settingKey);
        }
    }
}
