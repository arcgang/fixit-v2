// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named SlaOutcome elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SlaOutcome {
  @JsonProperty("MET") MET,
  @JsonProperty("BREACHED") BREACHED,
  @JsonProperty("PENDING") PENDING
}
