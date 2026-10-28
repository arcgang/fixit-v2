// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named ClockMode elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ClockMode {
  @JsonProperty("continuous") CONTINUOUS,
  @JsonProperty("shift_hours_only") SHIFT_HOURS_ONLY
}
