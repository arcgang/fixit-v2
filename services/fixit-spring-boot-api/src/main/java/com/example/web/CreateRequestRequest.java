package com.example.web;

import com.example.contract.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

record CreateRequestRequest(
        @NotNull UUID machineId,
        @NotBlank String description,
        @NotNull Priority priority
) {}
