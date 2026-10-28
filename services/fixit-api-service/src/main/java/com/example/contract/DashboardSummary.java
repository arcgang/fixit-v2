// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named DashboardSummary elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;

public record DashboardSummary(
    @JsonProperty("id") String id,
    @JsonProperty("uid") String uid,
    @JsonProperty("url") String url,
    @JsonProperty("tags") List<String> tags,
    @JsonProperty("title") String title,
    @JsonProperty("created_at") Instant createdAt,
    @JsonProperty("updated_at") Instant updatedAt
) {}
