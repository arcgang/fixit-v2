package com.example.web;

import com.example.domain.MachineEntity;
import com.example.domain.MachineRepository;
import com.example.domain.MaintenanceRequestRepository;
import com.example.domain.SlaRecordRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private MaintenanceRequestRepository requestRepository;

    @Autowired
    private SlaRecordRepository slaRecordRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MachineEntity activeMachine;
    private MachineEntity inactiveMachine;

    private static final Instant FAR_PAST = Instant.now().minusSeconds(365 * 24 * 3600L);

    @BeforeEach
    void setUp() {
        activeMachine = new MachineEntity();
        activeMachine.setName("Lathe A");
        activeMachine.setIsActive(true);
        activeMachine.setCreatedAt(FAR_PAST);
        activeMachine.setUpdatedAt(FAR_PAST);
        machineRepository.save(activeMachine);

        inactiveMachine = new MachineEntity();
        inactiveMachine.setName("Press B");
        inactiveMachine.setIsActive(false);
        inactiveMachine.setCreatedAt(FAR_PAST);
        inactiveMachine.setUpdatedAt(FAR_PAST);
        machineRepository.save(inactiveMachine);
    }

    @Test
    void createRequestReturns201WithCorrectFieldsOnValidInput() throws Exception {
        Map<String, Object> body = Map.of(
                "machineId", activeMachine.getMachineId().toString(),
                "description", "Motor making grinding noise",
                "priority", "CRITICAL"
        );

        String response = mockMvc.perform(post("/api/v1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .header("Authorization", "Bearer test-operator-token"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode result = objectMapper.readTree(response);
        assertEquals("OPEN", result.get("status").asText(), "new request status must be OPEN");
        assertEquals("CRITICAL", result.get("priority").asText(), "priority must match submitted value");
        assertNotNull(result.get("requestId"), "requestId must be present");
        assertNotNull(result.get("referenceNumber"), "referenceNumber must be present");
        assertTrue(result.get("referenceNumber").asText().startsWith("REQ-"), "referenceNumber must start with REQ-");
        assertNotNull(result.get("createdAt"), "createdAt must be present");
        assertNotNull(result.get("slaDeadlineAt"), "slaDeadlineAt must be present");
        assertEquals("Motor making grinding noise", result.get("description").asText(), "description must match");
    }

    @Test
    void createRequestPersistsSlaRecordForActiveMachine() throws Exception {
        Map<String, Object> body = Map.of(
                "machineId", activeMachine.getMachineId().toString(),
                "description", "Conveyor belt slipping",
                "priority", "MEDIUM"
        );

        String response = mockMvc.perform(post("/api/v1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .header("Authorization", "Bearer test-operator-token"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode result = objectMapper.readTree(response);
        UUID requestId = UUID.fromString(result.get("requestId").asText());

        // Verify sla_record row was created alongside the maintenance_request
        assertTrue(slaRecordRepository.findByRequestId(requestId).isPresent(),
                "sla_record row must exist for the newly created request");
    }

    @Test
    void createRequestSlaThresholdIsCorrectForCritical() throws Exception {
        Map<String, Object> body = Map.of(
                "machineId", activeMachine.getMachineId().toString(),
                "description", "Emergency fault",
                "priority", "CRITICAL"
        );

        String response = mockMvc.perform(post("/api/v1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .header("Authorization", "Bearer test-operator-token"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode result = objectMapper.readTree(response);
        assertEquals(60L, result.get("slaThresholdMinutes").asLong(),
                "CRITICAL priority must have 60-minute SLA threshold");
    }

    @Test
    void createRequestSlaThresholdIsCorrectForMedium() throws Exception {
        Map<String, Object> body = Map.of(
                "machineId", activeMachine.getMachineId().toString(),
                "description", "Moderate fault",
                "priority", "MEDIUM"
        );

        String response = mockMvc.perform(post("/api/v1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .header("Authorization", "Bearer test-operator-token"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode result = objectMapper.readTree(response);
        assertEquals(240L, result.get("slaThresholdMinutes").asLong(),
                "MEDIUM priority must have 240-minute SLA threshold");
    }

    @Test
    void createRequestSlaThresholdIsCorrectForLow() throws Exception {
        Map<String, Object> body = Map.of(
                "machineId", activeMachine.getMachineId().toString(),
                "description", "Low priority fault",
                "priority", "LOW"
        );

        String response = mockMvc.perform(post("/api/v1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .header("Authorization", "Bearer test-operator-token"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode result = objectMapper.readTree(response);
        assertEquals(480L, result.get("slaThresholdMinutes").asLong(),
                "LOW priority must have 480-minute SLA threshold");
    }

    @Test
    void createRequestReturns422WhenMachineNotFound() throws Exception {
        Map<String, Object> body = Map.of(
                "machineId", UUID.randomUUID().toString(),
                "description", "Some fault",
                "priority", "LOW"
        );

        String response = mockMvc.perform(post("/api/v1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .header("Authorization", "Bearer test-operator-token"))
                .andExpect(status().isUnprocessableEntity())
                .andReturn().getResponse().getContentAsString();

        JsonNode error = objectMapper.readTree(response);
        assertEquals("MACHINE_NOT_FOUND_OR_INACTIVE", error.get("code").asText(),
                "error code must be MACHINE_NOT_FOUND_OR_INACTIVE when machine does not exist");
    }

    @Test
    void createRequestReturns422WhenMachineIsInactive() throws Exception {
        Map<String, Object> body = Map.of(
                "machineId", inactiveMachine.getMachineId().toString(),
                "description", "Some fault",
                "priority", "LOW"
        );

        String response = mockMvc.perform(post("/api/v1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .header("Authorization", "Bearer test-operator-token"))
                .andExpect(status().isUnprocessableEntity())
                .andReturn().getResponse().getContentAsString();

        JsonNode error = objectMapper.readTree(response);
        assertEquals("MACHINE_NOT_FOUND_OR_INACTIVE", error.get("code").asText(),
                "error code must be MACHINE_NOT_FOUND_OR_INACTIVE when machine is inactive");
    }

    @Test
    void createRequestReturns400WhenDescriptionIsBlank() throws Exception {
        Map<String, Object> body = Map.of(
                "machineId", activeMachine.getMachineId().toString(),
                "description", "",
                "priority", "LOW"
        );

        String response = mockMvc.perform(post("/api/v1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .header("Authorization", "Bearer test-operator-token"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        JsonNode error = objectMapper.readTree(response);
        assertNotNull(error.get("details"), "validation error must include 'details' with per-field errors");
        assertTrue(error.get("details").size() > 0, "details must list at least one field violation");
    }

    @Test
    void createRequestReturns400WhenMachineIdIsNull() throws Exception {
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("description", "Some fault");
        body.put("priority", "LOW");

        String response = mockMvc.perform(post("/api/v1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .header("Authorization", "Bearer test-operator-token"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        JsonNode error = objectMapper.readTree(response);
        assertNotNull(error.get("details"), "missing machineId must produce per-field validation error");
    }

    @Test
    void createRequestReturns400WhenPriorityIsNull() throws Exception {
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("machineId", activeMachine.getMachineId().toString());
        body.put("description", "Some fault");

        String response = mockMvc.perform(post("/api/v1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .header("Authorization", "Bearer test-operator-token"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        JsonNode error = objectMapper.readTree(response);
        assertNotNull(error.get("details"), "missing priority must produce per-field validation error");
    }
}
