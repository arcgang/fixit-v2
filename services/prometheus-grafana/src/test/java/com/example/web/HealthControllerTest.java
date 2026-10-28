package com.example.web;

import com.example.health.ComponentProbe;
import com.example.health.ProbeResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthController.class)
@Import(HealthService.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "prometheusProbe")
    private ComponentProbe prometheusProbe;

    @MockBean(name = "grafanaProbe")
    private ComponentProbe grafanaProbe;

    @MockBean(name = "alertmanagerProbe")
    private ComponentProbe alertmanagerProbe;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void allHealthy_returns200WithHealthyStatus() throws Exception {
        when(prometheusProbe.name()).thenReturn("prometheus");
        when(prometheusProbe.probe()).thenReturn(ProbeResult.healthy());
        when(grafanaProbe.name()).thenReturn("grafana");
        when(grafanaProbe.probe()).thenReturn(ProbeResult.healthy());
        when(alertmanagerProbe.name()).thenReturn("alertmanager");
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.healthy());

        MvcResult result = mockMvc.perform(get("/healthz"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals("healthy", body.get("status").asText());
        assertTrue(body.has("checked_at"));
        assertNotNull(body.get("checked_at").asText());
        assertEquals(3, body.get("components").size());
    }

    @Test
    void allHealthy_returns200WithAllComponentsHealthy() throws Exception {
        when(prometheusProbe.name()).thenReturn("prometheus");
        when(prometheusProbe.probe()).thenReturn(ProbeResult.healthy());
        when(grafanaProbe.name()).thenReturn("grafana");
        when(grafanaProbe.probe()).thenReturn(ProbeResult.healthy());
        when(alertmanagerProbe.name()).thenReturn("alertmanager");
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.healthy());

        MvcResult result = mockMvc.perform(get("/healthz"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode components = mapper.readTree(result.getResponse().getContentAsString()).get("components");
        for (JsonNode c : components) {
            assertEquals("healthy", c.get("status").asText());
        }
    }

    @Test
    void oneDegraded_returns503WithDegradedStatus() throws Exception {
        when(prometheusProbe.name()).thenReturn("prometheus");
        when(prometheusProbe.probe()).thenReturn(ProbeResult.healthy());
        when(grafanaProbe.name()).thenReturn("grafana");
        when(grafanaProbe.probe()).thenReturn(ProbeResult.degraded("scrape lag detected"));
        when(alertmanagerProbe.name()).thenReturn("alertmanager");
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.healthy());

        MvcResult result = mockMvc.perform(get("/healthz"))
                .andExpect(status().isServiceUnavailable())
                .andReturn();

        JsonNode body = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals("degraded", body.get("status").asText());

        JsonNode components = body.get("components");
        assertEquals(3, components.size());

        boolean foundDegraded = false;
        for (JsonNode c : components) {
            if ("grafana".equals(c.get("name").asText())) {
                assertEquals("degraded", c.get("status").asText());
                assertEquals("scrape lag detected", c.get("message").asText());
                foundDegraded = true;
            }
        }
        assertTrue(foundDegraded, "grafana component must be present with degraded status");
    }

    @Test
    void allUnhealthy_returns503WithUnhealthyStatus() throws Exception {
        when(prometheusProbe.name()).thenReturn("prometheus");
        when(prometheusProbe.probe()).thenReturn(ProbeResult.unhealthy("connection refused"));
        when(grafanaProbe.name()).thenReturn("grafana");
        when(grafanaProbe.probe()).thenReturn(ProbeResult.unhealthy("timeout"));
        when(alertmanagerProbe.name()).thenReturn("alertmanager");
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.unhealthy("not reachable"));

        MvcResult result = mockMvc.perform(get("/healthz"))
                .andExpect(status().isServiceUnavailable())
                .andReturn();

        JsonNode body = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals("unhealthy", body.get("status").asText());

        JsonNode components = body.get("components");
        assertEquals(3, components.size());
        for (JsonNode c : components) {
            assertEquals("unhealthy", c.get("status").asText());
        }
    }

    @Test
    void response_hasRequiredFields_statusAndComponents() throws Exception {
        when(prometheusProbe.name()).thenReturn("prometheus");
        when(prometheusProbe.probe()).thenReturn(ProbeResult.healthy());
        when(grafanaProbe.name()).thenReturn("grafana");
        when(grafanaProbe.probe()).thenReturn(ProbeResult.healthy());
        when(alertmanagerProbe.name()).thenReturn("alertmanager");
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.healthy());

        MvcResult result = mockMvc.perform(get("/healthz"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(body.has("status"), "response must have 'status' field");
        assertTrue(body.has("components"), "response must have 'components' field");
    }

    @Test
    void degradedBeatsDegradedWorstStatus_degradedAndHealthy_yieldsDegraded() throws Exception {
        when(prometheusProbe.name()).thenReturn("prometheus");
        when(prometheusProbe.probe()).thenReturn(ProbeResult.degraded("slow"));
        when(grafanaProbe.name()).thenReturn("grafana");
        when(grafanaProbe.probe()).thenReturn(ProbeResult.healthy());
        when(alertmanagerProbe.name()).thenReturn("alertmanager");
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.healthy());

        MvcResult result = mockMvc.perform(get("/healthz"))
                .andExpect(status().isServiceUnavailable())
                .andReturn();

        JsonNode body = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals("degraded", body.get("status").asText());
    }

    @Test
    void unhealthyBeatsDegrade_unhealthyAndDegraded_yieldsUnhealthy() throws Exception {
        when(prometheusProbe.name()).thenReturn("prometheus");
        when(prometheusProbe.probe()).thenReturn(ProbeResult.unhealthy("down"));
        when(grafanaProbe.name()).thenReturn("grafana");
        when(grafanaProbe.probe()).thenReturn(ProbeResult.degraded("slow"));
        when(alertmanagerProbe.name()).thenReturn("alertmanager");
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.healthy());

        MvcResult result = mockMvc.perform(get("/healthz"))
                .andExpect(status().isServiceUnavailable())
                .andReturn();

        JsonNode body = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals("unhealthy", body.get("status").asText());
    }

    @Test
    void componentNames_matchPrometheusGrafanaAlertmanager() throws Exception {
        when(prometheusProbe.name()).thenReturn("prometheus");
        when(prometheusProbe.probe()).thenReturn(ProbeResult.healthy());
        when(grafanaProbe.name()).thenReturn("grafana");
        when(grafanaProbe.probe()).thenReturn(ProbeResult.healthy());
        when(alertmanagerProbe.name()).thenReturn("alertmanager");
        when(alertmanagerProbe.probe()).thenReturn(ProbeResult.healthy());

        MvcResult result = mockMvc.perform(get("/healthz"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode components = mapper.readTree(result.getResponse().getContentAsString()).get("components");
        List<String> names = new java.util.ArrayList<>();
        for (JsonNode c : components) {
            names.add(c.get("name").asText());
        }
        assertTrue(names.contains("prometheus"), "components must include 'prometheus'");
        assertTrue(names.contains("grafana"), "components must include 'grafana'");
        assertTrue(names.contains("alertmanager"), "components must include 'alertmanager'");
    }
}
