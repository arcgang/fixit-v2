// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named ErrorBody elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ErrorBody(
    @JsonProperty("code") String code,
    @JsonProperty("details") List<Map<String, Object>> details,
    @JsonProperty("message") String message,
    @JsonProperty("correlation_id") UUID correlationId
) {}
