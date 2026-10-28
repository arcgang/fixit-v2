// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named SloStatus elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record SloStatus(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("status") String status,
    @JsonProperty("target") Double target,
    @JsonProperty("evaluated_at") Instant evaluatedAt,
    @JsonProperty("window_hours") Long windowHours,
    @JsonProperty("current_burn_rate") Double currentBurnRate,
    @JsonProperty("error_budget_remaining_pct") Double errorBudgetRemainingPct
) {}
