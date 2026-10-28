package com.example.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "machine")
public class MachineEntity {

    @Id
    @Column(name = "machine_id", nullable = false, updatable = false)
    private UUID machineId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "asset_reference")
    private String assetReference;

    @Column(name = "location")
    private String location;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deactivated_at")
    private Instant deactivatedAt;

    @Column(name = "deactivated_by_user_id")
    private UUID deactivatedByUserId;

    protected MachineEntity() {}

    public UUID getMachineId() { return machineId; }
    public String getName() { return name; }
    public String getAssetReference() { return assetReference; }
    public String getLocation() { return location; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getDeactivatedAt() { return deactivatedAt; }
    public UUID getDeactivatedByUserId() { return deactivatedByUserId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MachineEntity m)) return false;
        return machineId != null && machineId.equals(m.machineId);
    }

    @Override
    public int hashCode() {
        return machineId != null ? machineId.hashCode() : 0;
    }
}
