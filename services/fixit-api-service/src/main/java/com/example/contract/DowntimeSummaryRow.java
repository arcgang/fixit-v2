// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named DowntimeSummaryRow elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.UUID;

public record DowntimeSummaryRow(
    @JsonProperty("machineId") UUID machineId,
    @JsonProperty("machineName") String machineName,
    @JsonProperty("periodIncomplete") Boolean periodIncomplete,
    @JsonProperty("totalDowntimeHours") BigDecimal totalDowntimeHours,
    @JsonProperty("resolvedRequestCount") Long resolvedRequestCount,
    @JsonProperty("totalDowntimeMinutes") Long totalDowntimeMinutes
) {}
