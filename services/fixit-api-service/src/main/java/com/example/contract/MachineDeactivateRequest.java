// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named MachineDeactivateRequest elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MachineDeactivateRequest(
    @JsonProperty("confirm") Boolean confirm
) {}
