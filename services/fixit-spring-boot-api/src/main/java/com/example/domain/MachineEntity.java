package com.example.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    public UUID getMachineId() { return machineId; }
    public void setMachineId(UUID machineId) { this.machineId = machineId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAssetReference() { return assetReference; }
    public void setAssetReference(String assetReference) { this.assetReference = assetReference; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Instant getDeactivatedAt() { return deactivatedAt; }
    public void setDeactivatedAt(Instant deactivatedAt) { this.deactivatedAt = deactivatedAt; }

    public UUID getDeactivatedByUserId() { return deactivatedByUserId; }
    public void setDeactivatedByUserId(UUID deactivatedByUserId) { this.deactivatedByUserId = deactivatedByUserId; }
}
