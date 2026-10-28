// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named AlertWebhookPayload elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

public record AlertWebhookPayload(
    @JsonProperty("alerts") List<InboundAlert> alerts,
    @JsonProperty("source") String source,
    @JsonProperty("correlation_id") UUID correlationId
) {}
