package com.example.domain;

import com.example.contract.Priority;
import com.example.contract.RequestStatus;
import com.example.contract.SlaOutcome;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "maintenance_request")
public class MaintenanceRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "request_id", nullable = false, updatable = false)
    private UUID requestId;

    @Column(name = "machine_id", nullable = false)
    private UUID machineId;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status = RequestStatus.OPEN;

    @Column(name = "created_by_user_id", nullable = false)
    private UUID createdByUserId;

    @Column(name = "created_by_display_name", nullable = false)
    private String createdByDisplayName;

    @Column(name = "assigned_to_user_id")
    private UUID assignedToUserId;

    @Column(name = "assigned_to_display_name")
    private String assignedToDisplayName;

    @Column(name = "created_at", nullable = false)
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
    private SlaOutcome slaOutcome;

    @Column(name = "out_of_hours_flag", nullable = false)
    private Boolean outOfHoursFlag = false;

    @Column(name = "reference_number", nullable = false, unique = true)
    private String referenceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", insertable = false, updatable = false)
    private MachineEntity machine;

    public UUID getRequestId() { return requestId; }
    public void setRequestId(UUID requestId) { this.requestId = requestId; }

    public UUID getMachineId() { return machineId; }
    public void setMachineId(UUID machineId) { this.machineId = machineId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public UUID getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(UUID createdByUserId) { this.createdByUserId = createdByUserId; }

    public String getCreatedByDisplayName() { return createdByDisplayName; }
    public void setCreatedByDisplayName(String createdByDisplayName) { this.createdByDisplayName = createdByDisplayName; }

    public UUID getAssignedToUserId() { return assignedToUserId; }
    public void setAssignedToUserId(UUID assignedToUserId) { this.assignedToUserId = assignedToUserId; }

    public String getAssignedToDisplayName() { return assignedToDisplayName; }
    public void setAssignedToDisplayName(String assignedToDisplayName) { this.assignedToDisplayName = assignedToDisplayName; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant assignedAt) { this.assignedAt = assignedAt; }

    public Instant getWorkStartedAt() { return workStartedAt; }
    public void setWorkStartedAt(Instant workStartedAt) { this.workStartedAt = workStartedAt; }

    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }

    public String getResolutionNote() { return resolutionNote; }
    public void setResolutionNote(String resolutionNote) { this.resolutionNote = resolutionNote; }

    public Instant getSlaDeadlineAt() { return slaDeadlineAt; }
    public void setSlaDeadlineAt(Instant slaDeadlineAt) { this.slaDeadlineAt = slaDeadlineAt; }

    public SlaOutcome getSlaOutcome() { return slaOutcome; }
    public void setSlaOutcome(SlaOutcome slaOutcome) { this.slaOutcome = slaOutcome; }

    public Boolean getOutOfHoursFlag() { return outOfHoursFlag; }
    public void setOutOfHoursFlag(Boolean outOfHoursFlag) { this.outOfHoursFlag = outOfHoursFlag; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public MachineEntity getMachine() { return machine; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MaintenanceRequestEntity)) return false;
        MaintenanceRequestEntity that = (MaintenanceRequestEntity) o;
        return Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() { return Objects.hash(requestId); }
}
