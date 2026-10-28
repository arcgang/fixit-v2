// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named MachineHistoryResponse elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record MachineHistoryResponse(
    @JsonProperty("data") List<MaintenanceRequest> data,
    @JsonProperty("page") CursorPage page
) {}
