// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named ExportRequest elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

public record ExportRequest(
    @JsonProperty("status") RequestStatus status,
    @JsonProperty("priority") Priority priority,
    @JsonProperty("createdAt") Instant createdAt,
    @JsonProperty("machineId") UUID machineId,
    @JsonProperty("requestId") UUID requestId,
    @JsonProperty("assignedAt") Instant assignedAt,
    @JsonProperty("outOfHours") Boolean outOfHours,
    @JsonProperty("resolvedAt") Instant resolvedAt,
    @JsonProperty("description") String description,
    @JsonProperty("machineName") String machineName,
    @JsonProperty("slaBreached") Boolean slaBreached,
    @JsonProperty("slaTargetAt") Instant slaTargetAt,
    @JsonProperty("inProgressAt") Instant inProgressAt,
    @JsonProperty("resolutionNote") String resolutionNote,
    @JsonProperty("downtimeMinutes") Long downtimeMinutes,
    @JsonProperty("assignedToUserId") UUID assignedToUserId,
    @JsonProperty("submittedByUserId") UUID submittedByUserId
) {}
