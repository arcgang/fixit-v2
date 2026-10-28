// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named ScrapeTarget elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Map;

public record ScrapeTarget(
    @JsonProperty("job") String job,
    @JsonProperty("state") String state,
    @JsonProperty("labels") Map<String, String> labels,
    @JsonProperty("instance") String instance,
    @JsonProperty("last_error") String lastError,
    @JsonProperty("last_scrape") Instant lastScrape
) {}
