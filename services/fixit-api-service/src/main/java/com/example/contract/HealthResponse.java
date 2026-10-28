// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named HealthResponse elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;

public record HealthResponse(
    @JsonProperty("status") String status,
    @JsonProperty("checked_at") Instant checkedAt,
    @JsonProperty("components") List<ComponentHealth> components
) {}
