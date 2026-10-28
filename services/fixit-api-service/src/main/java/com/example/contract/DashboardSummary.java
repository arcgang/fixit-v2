// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named DashboardSummary elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record DashboardSummary(
    @JsonProperty("openCount") Long openCount,
    @JsonProperty("resolvedCount") Long resolvedCount,
    @JsonProperty("recentRequests") List<MaintenanceRequest> recentRequests,
    @JsonProperty("slaAtRiskCount") Long slaAtRiskCount,
    @JsonProperty("inProgressCount") Long inProgressCount,
    @JsonProperty("slaBreachedCount") Long slaBreachedCount
) {}
