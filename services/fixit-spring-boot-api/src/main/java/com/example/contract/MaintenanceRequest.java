// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named MaintenanceRequest elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

public record MaintenanceRequest(
    @JsonProperty("status") RequestStatus status,
    @JsonProperty("priority") Priority priority,
    @JsonProperty("createdAt") Instant createdAt,
    @JsonProperty("machineId") UUID machineId,
    @JsonProperty("requestId") UUID requestId,
    @JsonProperty("slaStatus") SlaStatus slaStatus,
    @JsonProperty("assignedAt") Instant assignedAt,
    @JsonProperty("resolvedAt") Instant resolvedAt,
    @JsonProperty("slaOutcome") SlaOutcome slaOutcome,
    @JsonProperty("description") String description,
    @JsonProperty("machineName") String machineName,
    @JsonProperty("slaDeadlineAt") Instant slaDeadlineAt,
    @JsonProperty("workStartedAt") Instant workStartedAt,
    @JsonProperty("outOfHoursFlag") Boolean outOfHoursFlag,
    @JsonProperty("resolutionNote") String resolutionNote,
    @JsonProperty("createdByUserId") UUID createdByUserId,
    @JsonProperty("referenceNumber") String referenceNumber,
    @JsonProperty("assignedToUserId") UUID assignedToUserId,
    @JsonProperty("slaThresholdMinutes") Long slaThresholdMinutes,
    @JsonProperty("createdByDisplayName") String createdByDisplayName,
    @JsonProperty("assignedToDisplayName") String assignedToDisplayName
) {}
