package com.example.domain;

import com.example.contract.Priority;
import com.example.contract.SlaOutcome;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sla_record")
public class SlaRecordEntity {

    @Id
    @Column(name = "sla_record_id", nullable = false, updatable = false)
    private UUID slaRecordId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private MaintenanceRequestEntity request;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    @Column(name = "sla_threshold_minutes", nullable = false)
    private int slaThresholdMinutes;

    @Column(name = "sla_deadline_at", nullable = false)
    private Instant slaDeadlineAt;

    @Column(name = "sla_clock_mode", nullable = false)
    private String slaClockMode;

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

    protected SlaRecordEntity() {}

    public static SlaRecordEntity create(
            MaintenanceRequestEntity request,
            Priority priority,
            int slaThresholdMinutes,
            Instant slaDeadlineAt,
            Instant now) {
        SlaRecordEntity e = new SlaRecordEntity();
        e.slaRecordId = UUID.randomUUID();
        e.request = request;
        e.priority = priority;
        e.slaThresholdMinutes = slaThresholdMinutes;
        e.slaDeadlineAt = slaDeadlineAt;
        e.slaClockMode = "CONTINUOUS";
        e.createdAt = now;
        e.updatedAt = now;
        return e;
    }

    public UUID getSlaRecordId() { return slaRecordId; }
    public MaintenanceRequestEntity getRequest() { return request; }
    public Priority getPriority() { return priority; }
    public int getSlaThresholdMinutes() { return slaThresholdMinutes; }
    public Instant getSlaDeadlineAt() { return slaDeadlineAt; }
    public String getSlaClockMode() { return slaClockMode; }
    public SlaOutcome getSlaOutcome() { return slaOutcome; }
    public Integer getBreachDurationMinutes() { return breachDurationMinutes; }
    public Instant getEvaluatedAt() { return evaluatedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SlaRecordEntity s)) return false;
        return slaRecordId != null && slaRecordId.equals(s.slaRecordId);
    }

    @Override
    public int hashCode() {
        return slaRecordId != null ? slaRecordId.hashCode() : 0;
    }
}
