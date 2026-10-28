package com.example.web;

import com.example.contract.CreateRequestBody;
import com.example.contract.ErrorBody;
import com.example.contract.MaintenanceRequest;
import com.example.contract.Priority;
import com.example.contract.RequestStatus;
import com.example.contract.SlaOutcome;
import com.example.contract.SlaStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.config.SecurityConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
@Import({SecurityConfig.class, RequestControllerTest.MeterConfig.class})
class RequestControllerTest {

    @TestConfiguration
    static class MeterConfig {
        @Bean
        MeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }
    }

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RequestService requestService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static final UUID MACHINE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID REQUEST_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID USER_ID   = UUID.fromString("00000000-0000-0000-0000-000000000003");

    @Test
    void postRequests_withNoJwt_returns401() throws Exception {
        String body = objectMapper.writeValueAsString(
                new CreateRequestBody(Priority.CRITICAL, MACHINE_ID, "Pump broken"));
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postRequests_withTechnicianJwt_returns403() throws Exception {
        String body = objectMapper.writeValueAsString(
                new CreateRequestBody(Priority.CRITICAL, MACHINE_ID, "Pump broken"));
        mockMvc.perform(post("/requests")
                        .with(jwt().authorities(
                                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_TECHNICIAN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void postRequests_withOperatorJwt_missingDescription_returns400WithDescriptionDetail() throws Exception {
        RequestService.ValidationException ex = new RequestService.ValidationException(
                List.of(Map.of("field", "description", "message", "must not be blank")));
        when(requestService.createRequest(any(), any(), any())).thenThrow(ex);

        String body = objectMapper.writeValueAsString(
                new CreateRequestBody(Priority.CRITICAL, MACHINE_ID, ""));
        mockMvc.perform(post("/requests")
                        .with(operatorJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0].field").value("description"));
    }

    @Test
    void postRequests_withOperatorJwt_inactiveMachine_returns422() throws Exception {
        when(requestService.createRequest(any(), any(), any()))
                .thenThrow(new RequestService.MachineNotFoundOrInactiveException());

        String body = objectMapper.writeValueAsString(
                new CreateRequestBody(Priority.CRITICAL, MACHINE_ID, "Pump broken"));
        mockMvc.perform(post("/requests")
                        .with(operatorJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("MACHINE_NOT_FOUND_OR_INACTIVE"));
    }

    @Test
    void postRequests_withOperatorJwt_validRequest_returns201WithExpectedFields() throws Exception {
        Instant createdAt   = Instant.parse("2026-10-28T10:00:00Z");
        Instant slaDeadline = Instant.parse("2026-10-28T11:00:00Z");
        MaintenanceRequest response = new MaintenanceRequest(
                RequestStatus.OPEN, Priority.CRITICAL, createdAt, MACHINE_ID, REQUEST_ID,
                SlaStatus.ON_TRACK, null, null, null,
                "Pump broken", "Press A", slaDeadline, null, false, null,
                USER_ID, "REQ-20261028-0001", null, 60L, "operator1", null);
        when(requestService.createRequest(any(), any(), any())).thenReturn(response);

        String body = objectMapper.writeValueAsString(
                new CreateRequestBody(Priority.CRITICAL, MACHINE_ID, "Pump broken"));
        mockMvc.perform(post("/requests")
                        .with(operatorJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.slaStatus").value("ON_TRACK"))
                .andExpect(jsonPath("$.referenceNumber").value(matchesPattern("REQ-\\d{8}-\\d{4}")))
                .andExpect(jsonPath("$.slaThresholdMinutes").value(60))
                .andExpect(jsonPath("$.priority").value("CRITICAL"));
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor operatorJwt() {
        return jwt()
                .jwt(j -> j.subject(USER_ID.toString()).claim("preferred_username", "operator1"))
                .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_OPERATOR"));
    }
}
