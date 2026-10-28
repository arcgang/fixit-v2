// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named RequestStatus elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RequestStatus {
  @JsonProperty("OPEN") OPEN,
  @JsonProperty("IN_PROGRESS") IN_PROGRESS,
  @JsonProperty("RESOLVED") RESOLVED
}
