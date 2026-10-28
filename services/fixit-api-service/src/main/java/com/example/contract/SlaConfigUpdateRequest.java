// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named SlaConfigUpdateRequest elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record SlaConfigUpdateRequest(
    @JsonProperty("slaHours") BigDecimal slaHours,
    @JsonProperty("clockMode") ClockMode clockMode,
    @JsonProperty("shiftEndTime") String shiftEndTime,
    @JsonProperty("priorityLevel") Priority priorityLevel,
    @JsonProperty("shiftStartTime") String shiftStartTime
) {}
