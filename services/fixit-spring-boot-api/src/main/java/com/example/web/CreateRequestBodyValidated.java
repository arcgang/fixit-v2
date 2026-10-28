package com.example.web;

import com.example.contract.Priority;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateRequestBodyValidated(
        @JsonProperty("machineId") @NotNull UUID machineId,
        @JsonProperty("description") @NotBlank String description,
        @JsonProperty("priority") @NotNull Priority priority
) {}
