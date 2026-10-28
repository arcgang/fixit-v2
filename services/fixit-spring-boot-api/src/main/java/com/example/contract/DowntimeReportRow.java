// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named DowntimeReportRow elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record DowntimeReportRow(
    @JsonProperty("machineId") UUID machineId,
    @JsonProperty("machineName") String machineName,
    @JsonProperty("totalDowntimeHours") Double totalDowntimeHours,
    @JsonProperty("resolvedRequestCount") Long resolvedRequestCount,
    @JsonProperty("totalDowntimeMinutes") Long totalDowntimeMinutes
) {}
