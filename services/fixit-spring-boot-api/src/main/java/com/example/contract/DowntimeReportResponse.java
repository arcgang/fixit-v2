// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named DowntimeReportResponse elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record DowntimeReportResponse(
    @JsonProperty("rows") List<DowntimeReportRow> rows,
    @JsonProperty("period") Object period,
    @JsonProperty("periodIncomplete") Boolean periodIncomplete
) {}
