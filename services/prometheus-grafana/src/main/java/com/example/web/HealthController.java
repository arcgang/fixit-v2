package com.example.web;

import com.example.contract.HealthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/healthz")
    public ResponseEntity<HealthResponse> getHealth() {
        HealthResponse response = healthService.check(Instant.now());
        boolean healthy = "healthy".equals(response.status());
        return ResponseEntity
                .status(healthy ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}
