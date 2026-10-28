// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named DashboardPanel elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record DashboardPanel(
    @JsonProperty("id") Long id,
    @JsonProperty("type") String type,
    @JsonProperty("title") String title,
    @JsonProperty("targets") List<Map<String, Object>> targets,
    @JsonProperty("description") String description
) {}
