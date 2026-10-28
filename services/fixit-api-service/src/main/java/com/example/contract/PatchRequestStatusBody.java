// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Use this type; never declare a type named PatchRequestStatusBody elsewhere (a check refuses it).
package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record PatchRequestStatusBody(
    @JsonProperty("notes") String notes,
    @JsonProperty("status") RequestStatus status,
    @JsonProperty("resolutionNote") String resolutionNote,
    @JsonProperty("assignedToUserId") UUID assignedToUserId,
    @JsonProperty("assignedToDisplayName") String assignedToDisplayName
) {}
