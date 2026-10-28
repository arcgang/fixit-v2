// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named PatchMachineBody elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PatchMachineBody(
    @JsonProperty("name") String name,
    @JsonProperty("isActive") Boolean isActive,
    @JsonProperty("location") String location,
    @JsonProperty("assetReference") String assetReference,
    @JsonProperty("forceDeactivate") Boolean forceDeactivate
) {}
