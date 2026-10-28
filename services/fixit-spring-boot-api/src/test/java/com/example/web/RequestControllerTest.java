package com.example.web;

import com.example.config.TestSecurityConfig;
import com.example.domain.MachineEntity;
import com.example.domain.MachineRepository;
import com.example.domain.MaintenanceRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private MaintenanceRequestRepository maintenanceRequestRepository;

    private UUID activeMachineId;

    @BeforeEach
    void setUp() {
        maintenanceRequestRepository.deleteAll();
        machineRepository.deleteAll();

        MachineEntity machine = new MachineEntity();
        machine.setMachineId(UUID.randomUUID());
        machine.setName("CNC Mill #1");
        machine.setActive(true);
        machine.setCreatedAt(Instant.now());
        machine.setUpdatedAt(Instant.now());
        machineRepository.save(machine);
        activeMachineId = machine.getMachineId();
    }

    @Test
    void createRequest_returns201_withCorrectStatusAndReferenceNumber() throws Exception {
        Map<String, Object> body = Map.of(
                "machineId", activeMachineId.toString(),
                "description", "Grinding noise from spindle",
                "priority", "CRITICAL"
        );

        mockMvc.perform(post("/api/v1/requests")
                        .with(jwt()
                                .jwt(b -> b.subject(UUID.randomUUID().toString())
                                        .claim("preferred_username", "operator1"))
                                .authorities(new SimpleGrantedAuthority("ROLE_OPERATOR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andExpect(jsonPath("$.referenceNumber", matchesPattern("REQ-\\d{8}-\\d{4}")))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.machineId", is(activeMachineId.toString())))
                .andExpect(jsonPath("$.priority", is("CRITICAL")))
                .andExpect(jsonPath("$.requestId").exists());
    }

    @Test
    void createRequest_returns400_whenDescriptionBlank() throws Exception {
        Map<String, Object> body = Map.of(
                "machineId", activeMachineId.toString(),
                "description", "",
                "priority", "MEDIUM"
        );

        mockMvc.perform(post("/api/v1/requests")
                        .with(jwt()
                                .jwt(b -> b.subject(UUID.randomUUID().toString())
                                        .claim("preferred_username", "operator1"))
                                .authorities(new SimpleGrantedAuthority("ROLE_OPERATOR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void createRequest_returns400_whenMachineIdMissing() throws Exception {
        Map<String, Object> body = Map.of(
                "description", "Some fault",
                "priority", "LOW"
        );

        mockMvc.perform(post("/api/v1/requests")
                        .with(jwt()
                                .jwt(b -> b.subject(UUID.randomUUID().toString())
                                        .claim("preferred_username", "operator1"))
                                .authorities(new SimpleGrantedAuthority("ROLE_OPERATOR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void createRequest_returns422_whenMachineInactive() throws Exception {
        MachineEntity inactive = new MachineEntity();
        inactive.setMachineId(UUID.randomUUID());
        inactive.setName("Old Press");
        inactive.setActive(false);
        inactive.setCreatedAt(Instant.now());
        inactive.setUpdatedAt(Instant.now());
        machineRepository.save(inactive);

        Map<String, Object> body = Map.of(
                "machineId", inactive.getMachineId().toString(),
                "description", "Broken belt",
                "priority", "LOW"
        );

        mockMvc.perform(post("/api/v1/requests")
                        .with(jwt()
                                .jwt(b -> b.subject(UUID.randomUUID().toString())
                                        .claim("preferred_username", "operator1"))
                                .authorities(new SimpleGrantedAuthority("ROLE_OPERATOR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code", is("MACHINE_NOT_FOUND_OR_INACTIVE")));
    }

    @Test
    void createRequest_returns422_whenMachineNotFound() throws Exception {
        Map<String, Object> body = Map.of(
                "machineId", UUID.randomUUID().toString(),
                "description", "Unknown machine fault",
                "priority", "MEDIUM"
        );

        mockMvc.perform(post("/api/v1/requests")
                        .with(jwt()
                                .jwt(b -> b.subject(UUID.randomUUID().toString())
                                        .claim("preferred_username", "operator1"))
                                .authorities(new SimpleGrantedAuthority("ROLE_OPERATOR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code", is("MACHINE_NOT_FOUND_OR_INACTIVE")));
    }

    @Test
    void createRequest_returns403_whenRoleIsNotOperator() throws Exception {
        Map<String, Object> body = Map.of(
                "machineId", activeMachineId.toString(),
                "description", "Some fault",
                "priority", "LOW"
        );

        mockMvc.perform(post("/api/v1/requests")
                        .with(jwt()
                                .jwt(b -> b.subject(UUID.randomUUID().toString())
                                        .claim("preferred_username", "technician1"))
                                .authorities(new SimpleGrantedAuthority("ROLE_TECHNICIAN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createRequest_returns401_whenNoJwt() throws Exception {
        Map<String, Object> body = Map.of(
                "machineId", activeMachineId.toString(),
                "description", "Some fault",
                "priority", "LOW"
        );

        mockMvc.perform(post("/api/v1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createRequest_slaDeadlineAt_isCorrectForCriticalPriority() throws Exception {
        Map<String, Object> body = Map.of(
                "machineId", activeMachineId.toString(),
                "description", "Critical spindle fault",
                "priority", "CRITICAL"
        );

        mockMvc.perform(post("/api/v1/requests")
                        .with(jwt()
                                .jwt(b -> b.subject(UUID.randomUUID().toString())
                                        .claim("preferred_username", "operator1"))
                                .authorities(new SimpleGrantedAuthority("ROLE_OPERATOR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slaDeadlineAt").exists())
                .andExpect(jsonPath("$.slaThresholdMinutes", is(60)));
    }
}
