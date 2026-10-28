// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named DeactivationWarning elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

public record DeactivationWarning(
    @JsonProperty("machineId") UUID machineId,
    @JsonProperty("openRequests") List<Object> openRequests,
    @JsonProperty("openRequestCount") Long openRequestCount
) {}
