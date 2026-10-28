package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ComponentHealth(
    @JsonProperty("name") String name,
    @JsonProperty("status") String status,
    @JsonProperty("message") String message
) {}
