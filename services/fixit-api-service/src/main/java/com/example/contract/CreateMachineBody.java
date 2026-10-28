// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named CreateMachineBody elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateMachineBody(
    @JsonProperty("name") String name,
    @JsonProperty("location") String location,
    @JsonProperty("assetReference") String assetReference
) {}
