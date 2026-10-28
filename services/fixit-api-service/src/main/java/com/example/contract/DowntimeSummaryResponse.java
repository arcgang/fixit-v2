// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named DowntimeSummaryResponse elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;

public record DowntimeSummaryResponse(
    @JsonProperty("data") List<DowntimeSummaryRow> data,
    @JsonProperty("periodEnd") LocalDate periodEnd,
    @JsonProperty("periodStart") LocalDate periodStart,
    @JsonProperty("periodIncomplete") Boolean periodIncomplete
) {}
