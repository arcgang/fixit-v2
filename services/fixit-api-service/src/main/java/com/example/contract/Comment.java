// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named Comment elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

public record Comment(
    @JsonProperty("commentId") UUID commentId,
    @JsonProperty("createdAt") Instant createdAt,
    @JsonProperty("requestId") UUID requestId,
    @JsonProperty("authorRole") String authorRole,
    @JsonProperty("commentText") String commentText,
    @JsonProperty("authorUserId") UUID authorUserId,
    @JsonProperty("authorDisplayName") String authorDisplayName
) {}
