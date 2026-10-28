package com.example.domain;

import com.example.contract.Priority;
import com.example.contract.RequestStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "maintenance_request")
public class MaintenanceRequestEntity {

    @Id
    @Column(name = "request_id", nullable = false, updatable = false)
    private UUID requestId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "machine_id", nullable = false)
    private MachineEntity machine;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    @Column(name = "created_by_user_id", nullable = false)
    private UUID createdByUserId;

    @Column(name = "created_by_display_name", nullable = false)
    private String createdByDisplayName;

    @Column(name = "assigned_to_user_id")
    private UUID assignedToUserId;

    @Column(name = "assigned_to_display_name")
    private String assignedToDisplayName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "assigned_at")
    private Instant assignedAt;

    @Column(name = "work_started_at")
    private Instant workStartedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "resolution_note")
    private String resolutionNote;

    @Column(name = "sla_deadline_at", nullable = false)
    private Instant slaDeadlineAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "sla_outcome")
    private com.example.contract.SlaOutcome slaOutcome;

    @Column(name = "out_of_hours_flag", nullable = false)
    private boolean outOfHoursFlag;

    @Column(name = "reference_number", nullable = false, unique = true)
    private String referenceNumber;

    protected MaintenanceRequestEntity() {}

    public static MaintenanceRequestEntity create(
            UUID requestId,
            MachineEntity machine,
            String description,
            Priority priority,
            UUID createdByUserId,
            String createdByDisplayName,
            Instant createdAt,
            Instant slaDeadlineAt,
            String referenceNumber) {
        MaintenanceRequestEntity e = new MaintenanceRequestEntity();
        e.requestId = requestId;
        e.machine = machine;
        e.description = description;
        e.priority = priority;
        e.status = RequestStatus.OPEN;
        e.createdByUserId = createdByUserId;
        e.createdByDisplayName = createdByDisplayName;
        e.createdAt = createdAt;
        e.updatedAt = createdAt;
        e.slaDeadlineAt = slaDeadlineAt;
        e.referenceNumber = referenceNumber;
        e.outOfHoursFlag = false;
        return e;
    }

    public UUID getRequestId() { return requestId; }
    public MachineEntity getMachine() { return machine; }
    public String getDescription() { return description; }
    public Priority getPriority() { return priority; }
    public RequestStatus getStatus() { return status; }
    public UUID getCreatedByUserId() { return createdByUserId; }
    public String getCreatedByDisplayName() { return createdByDisplayName; }
    public UUID getAssignedToUserId() { return assignedToUserId; }
    public String getAssignedToDisplayName() { return assignedToDisplayName; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getAssignedAt() { return assignedAt; }
    public Instant getWorkStartedAt() { return workStartedAt; }
    public Instant getResolvedAt() { return resolvedAt; }
    public String getResolutionNote() { return resolutionNote; }
    public Instant getSlaDeadlineAt() { return slaDeadlineAt; }
    public com.example.contract.SlaOutcome getSlaOutcome() { return slaOutcome; }
    public boolean isOutOfHoursFlag() { return outOfHoursFlag; }
    public String getReferenceNumber() { return referenceNumber; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MaintenanceRequestEntity r)) return false;
        return requestId != null && requestId.equals(r.requestId);
    }

    @Override
    public int hashCode() {
        return requestId != null ? requestId.hashCode() : 0;
    }
}
