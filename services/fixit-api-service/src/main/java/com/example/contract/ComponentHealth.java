// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named ComponentHealth elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ComponentHealth(
    @JsonProperty("name") String name,
    @JsonProperty("status") String status,
    @JsonProperty("message") String message
) {}
