package com.example.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Component("prometheusProbe")
public class PrometheusProbe implements ComponentProbe {

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public PrometheusProbe(
            @Value("${observability.prometheus.url:http://prometheus:9090}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
        this.restTemplate.setRequestFactory(timeoutFactory());
    }

    private org.springframework.http.client.SimpleClientHttpRequestFactory timeoutFactory() {
        var f = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        f.setConnectTimeout((int) Duration.ofSeconds(3).toMillis());
        f.setReadTimeout((int) Duration.ofSeconds(3).toMillis());
        return f;
    }

    @Override
    public String name() {
        return "prometheus";
    }

    @Override
    public ProbeResult probe() {
        try {
            restTemplate.getForEntity(baseUrl + "/-/healthy", String.class);
            return ProbeResult.healthy();
        } catch (Exception e) {
            return ProbeResult.unhealthy(e.getMessage());
        }
    }
}
