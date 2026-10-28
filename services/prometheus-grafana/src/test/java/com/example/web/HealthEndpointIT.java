package com.example.web;

import com.example.health.ComponentProbe;
import com.example.health.ProbeResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AC1: GET /healthz is implemented to its OpenAPI contract.
 *
 * Runs against the full Spring Boot application context (all beans wired) to test the
 * seam between HealthController → HealthService → ComponentProbe beans — the join that
 * neither the controller unit test nor any probe unit test covers in isolation.
 */
@SpringBootTest
@AutoConfigureMockMvc
class HealthEndpointIT {

    @Autowired
    private MockMvc mockMvc;

    // Replace the real probe beans (which make external HTTP calls) with mocks so the
    // full wiring is tested without requiring live Prometheus/Grafana/Alertmanager.
    @MockBean(name = "prometheusProbe")
    private ComponentProbe prometheusProbe;

    @MockBean(name = "grafanaProbe")
    private ComponentProbe grafanaProbe;

    @MockBean(name = "alertmanagerProbe")
    private ComponentProbe alertmanagerProbe;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void stubProbeNames() {
        when(prometheusProbe.name()).thenReturn("prometheus");
        when(grafanaProbe.name()).thenReturn("grafana");
        when(alertmanagerProbe.name()).thenReturn("alertmanager");
    }

    // -------------------------------------------------------------------------
    // Precondition: endpoint must be mapped — a 404 means the story is not done
    // -------------------------------------------------------------------------

    @Test
    void precondition_getHealthz_isNotUnmapped() throws Exception {
        when(prometheusProbe.probe()).thenReturn(ProbeResult.healthy());
        when(grafanaProbe.probe()).thenReturn(ProbeResult.healthy());
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.healthy());

        MvcResult result = mockMvc.perform(get("/healthz")).andReturn();
        int httpStatus = result.getResponse().getStatus();
        assertNotEquals(404, httpStatus,
                "GET /healthz returned 404 — endpoint is not mapped; story is not implemented");
    }

    // -------------------------------------------------------------------------
    // AC1: HTTP status codes match the contract
    // -------------------------------------------------------------------------

    @Test
    void allComponentsHealthy_returns200() throws Exception {
        when(prometheusProbe.probe()).thenReturn(ProbeResult.healthy());
        when(grafanaProbe.probe()).thenReturn(ProbeResult.healthy());
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.healthy());

        mockMvc.perform(get("/healthz"))
                .andExpect(status().isOk());
    }

    @Test
    void anyComponentDegraded_returns503() throws Exception {
        when(prometheusProbe.probe()).thenReturn(ProbeResult.healthy());
        when(grafanaProbe.probe()).thenReturn(ProbeResult.degraded("scrape lag"));
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.healthy());

        mockMvc.perform(get("/healthz"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void anyComponentUnhealthy_returns503() throws Exception {
        when(prometheusProbe.probe()).thenReturn(ProbeResult.unhealthy("connection refused"));
        when(grafanaProbe.probe()).thenReturn(ProbeResult.healthy());
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.healthy());

        mockMvc.perform(get("/healthz"))
                .andExpect(status().isServiceUnavailable());
    }

    // -------------------------------------------------------------------------
    // AC1: Response body shape matches the OpenAPI contract
    // -------------------------------------------------------------------------

    @Test
    void responseBody_hasStatusField_withValidEnumValue() throws Exception {
        when(prometheusProbe.probe()).thenReturn(ProbeResult.healthy());
        when(grafanaProbe.probe()).thenReturn(ProbeResult.healthy());
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.healthy());

        MvcResult result = mockMvc.perform(get("/healthz"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        JsonNode body = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(body.has("status"), "response must have 'status' field");
        String status = body.get("status").asText();
        assertTrue(Set.of("healthy", "degraded", "unhealthy").contains(status),
                "status must be one of healthy|degraded|unhealthy, got: " + status);
    }

    @Test
    void responseBody_hasCheckedAt_asValidIso8601Timestamp() throws Exception {
        when(prometheusProbe.probe()).thenReturn(ProbeResult.healthy());
        when(grafanaProbe.probe()).thenReturn(ProbeResult.healthy());
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.healthy());

        MvcResult result = mockMvc.perform(get("/healthz")).andReturn();
        JsonNode body = mapper.readTree(result.getResponse().getContentAsString());

        assertTrue(body.has("checked_at"), "response must have 'checked_at' field");
        String checkedAt = body.get("checked_at").asText();
        assertDoesNotThrow(() -> Instant.parse(checkedAt),
                "checked_at must be a valid ISO-8601 instant, got: " + checkedAt);
    }

    @Test
    void responseBody_hasComponentsArray_withExactlyThreeEntries() throws Exception {
        when(prometheusProbe.probe()).thenReturn(ProbeResult.healthy());
        when(grafanaProbe.probe()).thenReturn(ProbeResult.healthy());
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.healthy());

        MvcResult result = mockMvc.perform(get("/healthz")).andReturn();
        JsonNode body = mapper.readTree(result.getResponse().getContentAsString());

        assertTrue(body.has("components"), "response must have 'components' field");
        assertTrue(body.get("components").isArray(), "'components' must be an array");
        assertEquals(3, body.get("components").size(),
                "components array must contain exactly 3 entries (prometheus, grafana, alertmanager)");
    }

    @Test
    void responseComponents_includeAllThreeProbes_namedPrometheusGrafanaAlertmanager() throws Exception {
        when(prometheusProbe.probe()).thenReturn(ProbeResult.healthy());
        when(grafanaProbe.probe()).thenReturn(ProbeResult.healthy());
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.healthy());

        MvcResult result = mockMvc.perform(get("/healthz")).andReturn();
        JsonNode components = mapper.readTree(result.getResponse().getContentAsString()).get("components");

        Set<String> names = new HashSet<>();
        for (JsonNode c : components) {
            assertTrue(c.has("name"), "each component must have a 'name' field");
            assertTrue(c.has("status"), "each component must have a 'status' field");
            String componentStatus = c.get("status").asText();
            assertTrue(Set.of("healthy", "degraded", "unhealthy").contains(componentStatus),
                    "component status must be one of healthy|degraded|unhealthy, got: " + componentStatus);
            names.add(c.get("name").asText());
        }
        assertEquals(Set.of("prometheus", "grafana", "alertmanager"), names,
                "components must be named exactly: prometheus, grafana, alertmanager");
    }

    @Test
    void responseComponent_includesMessageField_whenUnhealthy() throws Exception {
        when(prometheusProbe.probe()).thenReturn(ProbeResult.unhealthy("connection refused"));
        when(grafanaProbe.probe()).thenReturn(ProbeResult.healthy());
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.healthy());

        MvcResult result = mockMvc.perform(get("/healthz")).andReturn();
        JsonNode components = mapper.readTree(result.getResponse().getContentAsString()).get("components");

        boolean found = false;
        for (JsonNode c : components) {
            if ("prometheus".equals(c.get("name").asText())) {
                found = true;
                assertEquals("unhealthy", c.get("status").asText());
                assertEquals("connection refused", c.get("message").asText(),
                        "unhealthy component must carry the error message");
            }
        }
        assertTrue(found, "prometheus component must be present in the response");
    }

    // -------------------------------------------------------------------------
    // AC1: Top-level status reflects the worst component status (aggregate rule)
    // -------------------------------------------------------------------------

    @Test
    void aggregateStatus_allHealthy_yieldsHealthy() throws Exception {
        when(prometheusProbe.probe()).thenReturn(ProbeResult.healthy());
        when(grafanaProbe.probe()).thenReturn(ProbeResult.healthy());
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.healthy());

        MvcResult result = mockMvc.perform(get("/healthz")).andReturn();
        JsonNode body = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals("healthy", body.get("status").asText());
    }

    @Test
    void aggregateStatus_unhealthyBeatsDegraded() throws Exception {
        when(prometheusProbe.probe()).thenReturn(ProbeResult.unhealthy("down"));
        when(grafanaProbe.probe()).thenReturn(ProbeResult.degraded("slow"));
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.healthy());

        MvcResult result = mockMvc.perform(get("/healthz")).andReturn();
        JsonNode body = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals("unhealthy", body.get("status").asText(),
                "unhealthy must propagate over degraded as the aggregate status");
    }

    @Test
    void aggregateStatus_degradedBeatsHealthy() throws Exception {
        when(prometheusProbe.probe()).thenReturn(ProbeResult.healthy());
        when(grafanaProbe.probe()).thenReturn(ProbeResult.degraded("lag detected"));
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.healthy());

        MvcResult result = mockMvc.perform(get("/healthz")).andReturn();
        JsonNode body = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals("degraded", body.get("status").asText(),
                "degraded must propagate over healthy as the aggregate status");
    }

    @Test
    void statusField_and_httpStatusCode_areConsistent() throws Exception {
        // 200 ↔ "healthy"; 503 ↔ "degraded" or "unhealthy"
        when(prometheusProbe.probe()).thenReturn(ProbeResult.unhealthy("down"));
        when(grafanaProbe.probe()).thenReturn(ProbeResult.healthy());
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.healthy());

        MvcResult result = mockMvc.perform(get("/healthz")).andReturn();
        JsonNode body = mapper.readTree(result.getResponse().getContentAsString());
        String bodyStatus = body.get("status").asText();
        int httpCode = result.getResponse().getStatus();

        if ("healthy".equals(bodyStatus)) {
            assertEquals(200, httpCode,
                    "HTTP 200 must accompany status=healthy");
        } else {
            assertEquals(503, httpCode,
                    "HTTP 503 must accompany status=" + bodyStatus);
        }
    }
}
