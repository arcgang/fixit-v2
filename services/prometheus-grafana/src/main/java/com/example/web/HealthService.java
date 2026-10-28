package com.example.web;

import com.example.contract.ComponentHealth;
import com.example.contract.HealthResponse;
import com.example.health.ComponentProbe;
import com.example.health.ProbeResult;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class HealthService {

    private final List<ComponentProbe> probes;

    public HealthService(
            ComponentProbe prometheusProbe,
            ComponentProbe grafanaProbe,
            ComponentProbe alertmanagerProbe) {
        this.probes = List.of(prometheusProbe, grafanaProbe, alertmanagerProbe);
    }

    public HealthResponse check(Instant now) {
        List<ComponentHealth> components = probes.stream()
                .map(p -> {
                    ProbeResult r = p.probe();
                    return new ComponentHealth(p.name(), r.status(), r.message());
                })
                .toList();

        String aggregateStatus = aggregateStatus(components);
        return new HealthResponse(aggregateStatus, now, components);
    }

    private String aggregateStatus(List<ComponentHealth> components) {
        boolean anyUnhealthy = components.stream().anyMatch(c -> "unhealthy".equals(c.status()));
        if (anyUnhealthy) return "unhealthy";
        boolean anyDegraded = components.stream().anyMatch(c -> "degraded".equals(c.status()));
        if (anyDegraded) return "degraded";
        return "healthy";
    }
}
