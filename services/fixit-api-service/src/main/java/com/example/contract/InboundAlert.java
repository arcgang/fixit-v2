// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named InboundAlert elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Map;

public record InboundAlert(
    @JsonProperty("name") String name,
    @JsonProperty("state") String state,
    @JsonProperty("labels") Map<String, String> labels,
    @JsonProperty("fired_at") Instant firedAt,
    @JsonProperty("severity") String severity,
    @JsonProperty("annotations") Map<String, String> annotations,
    @JsonProperty("resolved_at") Instant resolvedAt,
    @JsonProperty("generator_url") String generatorUrl
) {}
