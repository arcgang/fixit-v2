// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named SlaStatus elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SlaStatus {
  @JsonProperty("ON_TRACK") ON_TRACK,
  @JsonProperty("AT_RISK") AT_RISK,
  @JsonProperty("BREACHED") BREACHED,
  @JsonProperty("MET") MET
}
