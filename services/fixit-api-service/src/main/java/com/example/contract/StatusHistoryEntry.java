// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named StatusHistoryEntry elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

public record StatusHistoryEntry(
    @JsonProperty("notes") String notes,
    @JsonProperty("toStatus") String toStatus,
    @JsonProperty("historyId") UUID historyId,
    @JsonProperty("requestId") UUID requestId,
    @JsonProperty("fromStatus") String fromStatus,
    @JsonProperty("transitionedAt") Instant transitionedAt,
    @JsonProperty("transitionedByRole") String transitionedByRole,
    @JsonProperty("transitionedByUserId") UUID transitionedByUserId
) {}
