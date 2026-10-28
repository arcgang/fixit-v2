package com.example.web;

import com.example.domain.MachineEntity;
import com.example.domain.MachineRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MachineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MachineEntity activeMachine;
    private MachineEntity inactiveMachine;

    @BeforeEach
    void setUp() {
        activeMachine = new MachineEntity();
        activeMachine.setName("Lathe A");
        activeMachine.setIsActive(true);
        activeMachine.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        activeMachine.setUpdatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        machineRepository.save(activeMachine);

        inactiveMachine = new MachineEntity();
        inactiveMachine.setName("Press B");
        inactiveMachine.setIsActive(false);
        inactiveMachine.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        inactiveMachine.setUpdatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        inactiveMachine.setDeactivatedAt(Instant.parse("2026-06-01T00:00:00Z"));
        machineRepository.save(inactiveMachine);
    }

    @Test
    void getMachinesReturnsOnlyActiveMachinesWhenActiveOnlyTrue() throws Exception {
        String body = mockMvc.perform(get("/api/v1/machines")
                        .param("activeOnly", "true")
                        .header("Authorization", "Bearer test-operator-token"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(body);
        JsonNode data = root.get("data");
        assertNotNull(data, "response must have 'data' field");
        assertEquals(1, data.size(), "only one active machine should be returned");
        assertEquals("Lathe A", data.get(0).get("name").asText(), "active machine name must match");
        assertTrue(data.get(0).get("isActive").asBoolean(), "returned machine must be active");
    }

    @Test
    void getMachinesReturnsAllMachinesWhenActiveOnlyFalse() throws Exception {
        String body = mockMvc.perform(get("/api/v1/machines")
                        .param("activeOnly", "false")
                        .header("Authorization", "Bearer test-supervisor-token"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(body);
        assertEquals(2, root.get("data").size(), "all machines (active and inactive) should be returned");
    }

    @Test
    void getMachinesDefaultsToActiveOnlyTrue() throws Exception {
        String body = mockMvc.perform(get("/api/v1/machines")
                        .header("Authorization", "Bearer test-operator-token"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(body);
        assertEquals(1, root.get("data").size(), "default activeOnly=true must filter inactive machines");
    }

    @Test
    void getMachinesResponseHasPageField() throws Exception {
        String body = mockMvc.perform(get("/api/v1/machines")
                        .header("Authorization", "Bearer test-operator-token"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(body);
        assertNotNull(root.get("page"), "response must include 'page' cursor metadata");
        assertNotNull(root.get("page").get("limit"), "page must include 'limit'");
    }

    @Test
    void getMachinesReturns401WhenNoAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/api/v1/machines"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMachinesReturnsExpectedMachineFields() throws Exception {
        String body = mockMvc.perform(get("/api/v1/machines")
                        .header("Authorization", "Bearer test-operator-token"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode machine = objectMapper.readTree(body).get("data").get(0);
        assertNotNull(machine.get("machineId"), "machineId must be present");
        assertNotNull(machine.get("name"), "name must be present");
        assertNotNull(machine.get("isActive"), "isActive must be present");
        assertNotNull(machine.get("createdAt"), "createdAt must be present");
    }
}
