package com.example.domain;

import com.example.contract.Priority;
import com.example.contract.SlaOutcome;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sla_record")
public class SlaRecordEntity {

    @Id
    @Column(name = "sla_record_id", nullable = false, updatable = false)
    private UUID slaRecordId;

    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    @Column(name = "sla_threshold_minutes", nullable = false)
    private Integer slaThresholdMinutes;

    @Column(name = "sla_deadline_at", nullable = false)
    private Instant slaDeadlineAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "sla_clock_mode", nullable = false)
    private SlaClockMode slaClockMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "sla_outcome")
    private SlaOutcome slaOutcome;

    @Column(name = "breach_duration_minutes")
    private Integer breachDurationMinutes;

    @Column(name = "evaluated_at")
    private Instant evaluatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UUID getSlaRecordId() { return slaRecordId; }
    public void setSlaRecordId(UUID slaRecordId) { this.slaRecordId = slaRecordId; }

    public UUID getRequestId() { return requestId; }
    public void setRequestId(UUID requestId) { this.requestId = requestId; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Integer getSlaThresholdMinutes() { return slaThresholdMinutes; }
    public void setSlaThresholdMinutes(Integer slaThresholdMinutes) { this.slaThresholdMinutes = slaThresholdMinutes; }

    public Instant getSlaDeadlineAt() { return slaDeadlineAt; }
    public void setSlaDeadlineAt(Instant slaDeadlineAt) { this.slaDeadlineAt = slaDeadlineAt; }

    public SlaClockMode getSlaClockMode() { return slaClockMode; }
    public void setSlaClockMode(SlaClockMode slaClockMode) { this.slaClockMode = slaClockMode; }

    public SlaOutcome getSlaOutcome() { return slaOutcome; }
    public void setSlaOutcome(SlaOutcome slaOutcome) { this.slaOutcome = slaOutcome; }

    public Integer getBreachDurationMinutes() { return breachDurationMinutes; }
    public void setBreachDurationMinutes(Integer breachDurationMinutes) { this.breachDurationMinutes = breachDurationMinutes; }

    public Instant getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(Instant evaluatedAt) { this.evaluatedAt = evaluatedAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
