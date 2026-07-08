package com.playground.playground.testdata.entity;

import com.playground.playground.domain.TestDataEntityType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(schema = "playground", name = "test_data_handles")
public class TestDataHandleEntity {

    @Id
    private String id;

    @Column(nullable = false, length = 64)
    private String namespace;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 64)
    private TestDataEntityType entityType;

    @Column(name = "entity_id", nullable = false, length = 36)
    private String entityId;

    @Column(length = 1024)
    private String metadata;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public TestDataEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(TestDataEntityType entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
