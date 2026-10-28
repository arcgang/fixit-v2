package com.example.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;

public record HealthResponse(
    @JsonProperty("status") String status,
    @JsonProperty("checked_at") Instant checkedAt,
    @JsonProperty("components") List<ComponentHealth> components
) {}
