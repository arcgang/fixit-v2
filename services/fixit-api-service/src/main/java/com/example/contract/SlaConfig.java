// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named SlaConfig elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SlaConfig(
    @JsonProperty("configId") UUID configId,
    @JsonProperty("slaHours") BigDecimal slaHours,
    @JsonProperty("clockMode") ClockMode clockMode,
    @JsonProperty("updatedAt") Instant updatedAt,
    @JsonProperty("shiftEndTime") String shiftEndTime,
    @JsonProperty("priorityLevel") Priority priorityLevel,
    @JsonProperty("shiftStartTime") String shiftStartTime,
    @JsonProperty("updatedByUserId") UUID updatedByUserId
) {}
