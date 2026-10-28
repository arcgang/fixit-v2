package com.example.web;

import com.example.contract.Priority;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbc;

    private UUID insertActiveMachine(String name) {
        UUID id = UUID.randomUUID();
        jdbc.update(
            "INSERT INTO machine (machine_id, name, is_active, created_at, updated_at) VALUES (?, ?, true, now(), now())",
            id, name
        );
        return id;
    }

    private UUID insertInactiveMachine(String name) {
        UUID id = UUID.randomUUID();
        jdbc.update(
            "INSERT INTO machine (machine_id, name, is_active, created_at, updated_at) VALUES (?, ?, false, now(), now())",
            id, name
        );
        return id;
    }

    @Test
    @WithMockUser(username = "operator-user", roles = "OPERATOR")
    void createRequest_validBody_returns201WithStatusOpenAndReferenceNumber() throws Exception {
        UUID machineId = insertActiveMachine("Lathe-01");

        Map<String, Object> body = Map.of(
            "machineId", machineId.toString(),
            "description", "Grinding noise from motor",
            "priority", "CRITICAL"
        );

        mockMvc.perform(post("/api/v1/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status", is("OPEN")))
            .andExpect(jsonPath("$.requestId").isNotEmpty())
            .andExpect(jsonPath("$.referenceNumber", matchesPattern("REQ-\\d{8}-\\d{4}")))
            .andExpect(jsonPath("$.createdAt").isNotEmpty())
            .andExpect(jsonPath("$.machineId", is(machineId.toString())))
            .andExpect(jsonPath("$.priority", is("CRITICAL")))
            .andExpect(jsonPath("$.description", is("Grinding noise from motor")));
    }

    @Test
    @WithMockUser(username = "operator-user", roles = "OPERATOR")
    void createRequest_inactiveMachine_returns422WithMachineNotFoundCode() throws Exception {
        UUID inactiveMachineId = insertInactiveMachine("Broken-Machine");

        Map<String, Object> body = Map.of(
            "machineId", inactiveMachineId.toString(),
            "description", "Something is wrong",
            "priority", "MEDIUM"
        );

        mockMvc.perform(post("/api/v1/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code", is("MACHINE_NOT_FOUND_OR_INACTIVE")))
            .andExpect(jsonPath("$.correlation_id").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "operator-user", roles = "OPERATOR")
    void createRequest_nonexistentMachineId_returns422WithMachineNotFoundCode() throws Exception {
        UUID nonExistentMachineId = UUID.randomUUID();

        Map<String, Object> body = Map.of(
            "machineId", nonExistentMachineId.toString(),
            "description", "Something is wrong",
            "priority", "LOW"
        );

        mockMvc.perform(post("/api/v1/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code", is("MACHINE_NOT_FOUND_OR_INACTIVE")));
    }

    @Test
    @WithMockUser(username = "operator-user", roles = "OPERATOR")
    void createRequest_missingDescription_returns400WithFieldDetails() throws Exception {
        UUID machineId = insertActiveMachine("Lathe-01");

        Map<String, Object> body = Map.of(
            "machineId", machineId.toString(),
            "priority", "CRITICAL"
        );

        mockMvc.perform(post("/api/v1/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")))
            .andExpect(jsonPath("$.details", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(username = "operator-user", roles = "OPERATOR")
    void createRequest_missingMachineId_returns400WithFieldDetails() throws Exception {
        Map<String, Object> body = Map.of(
            "description", "Some fault",
            "priority", "MEDIUM"
        );

        mockMvc.perform(post("/api/v1/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")))
            .andExpect(jsonPath("$.details", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(username = "operator-user", roles = "OPERATOR")
    void createRequest_missingPriority_returns400WithFieldDetails() throws Exception {
        UUID machineId = insertActiveMachine("Lathe-01");

        Map<String, Object> body = Map.of(
            "machineId", machineId.toString(),
            "description", "Some fault"
        );

        mockMvc.perform(post("/api/v1/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")))
            .andExpect(jsonPath("$.details", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void createRequest_unauthenticated_returns401() throws Exception {
        Map<String, Object> body = Map.of(
            "machineId", UUID.randomUUID().toString(),
            "description", "Some fault",
            "priority", "LOW"
        );

        mockMvc.perform(post("/api/v1/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void createRequest_technicianRole_returns403() throws Exception {
        UUID machineId = insertActiveMachine("Lathe-01");

        Map<String, Object> body = Map.of(
            "machineId", machineId.toString(),
            "description", "Some fault",
            "priority", "LOW"
        );

        mockMvc.perform(post("/api/v1/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "operator-user", roles = "OPERATOR")
    void createRequest_slaDeadlineComputedFromPriority() throws Exception {
        UUID machineId = insertActiveMachine("Lathe-01");

        Map<String, Object> body = Map.of(
            "machineId", machineId.toString(),
            "description", "Critical fault",
            "priority", "CRITICAL"
        );

        mockMvc.perform(post("/api/v1/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.slaDeadlineAt").isNotEmpty());
    }
}
