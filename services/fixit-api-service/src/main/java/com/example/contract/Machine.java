// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named Machine elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

public record Machine(
    @JsonProperty("name") String name,
    @JsonProperty("isActive") Boolean isActive,
    @JsonProperty("location") String location,
    @JsonProperty("createdAt") Instant createdAt,
    @JsonProperty("machineId") UUID machineId,
    @JsonProperty("deactivatedAt") Instant deactivatedAt,
    @JsonProperty("assetReference") String assetReference,
    @JsonProperty("deactivatedByUserId") UUID deactivatedByUserId
) {}
