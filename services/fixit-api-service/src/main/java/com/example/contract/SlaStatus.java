// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named SlaStatus elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SlaStatus {
  @JsonProperty("ok") OK,
  @JsonProperty("at_risk") AT_RISK,
  @JsonProperty("breached") BREACHED,
  @JsonProperty("resolved_within_sla") RESOLVED_WITHIN_SLA,
  @JsonProperty("resolved_breached") RESOLVED_BREACHED
}
