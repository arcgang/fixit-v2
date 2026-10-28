// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named SseEvent elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Map;

public record SseEvent(
    @JsonProperty("payload") Map<String, Object> payload,
    @JsonProperty("eventType") String eventType,
    @JsonProperty("occurredAt") Instant occurredAt
) {}
