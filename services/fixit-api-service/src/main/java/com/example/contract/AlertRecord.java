// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named AlertRecord elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AlertRecord(
    @JsonProperty("id") UUID id,
    @JsonProperty("name") String name,
    @JsonProperty("state") String state,
    @JsonProperty("labels") Map<String, String> labels,
    @JsonProperty("source") String source,
    @JsonProperty("fired_at") Instant firedAt,
    @JsonProperty("severity") String severity,
    @JsonProperty("created_at") Instant createdAt,
    @JsonProperty("updated_at") Instant updatedAt,
    @JsonProperty("annotations") Map<String, String> annotations,
    @JsonProperty("resolved_at") Instant resolvedAt,
    @JsonProperty("generator_url") String generatorUrl
) {}
