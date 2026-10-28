package com.example.web;

import com.example.config.TestSecurityConfig;
import com.example.domain.MachineEntity;
import com.example.domain.MachineRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class MachineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MachineRepository machineRepository;

    @BeforeEach
    void setUp() {
        machineRepository.deleteAll();
    }

    @Test
    void listMachines_returnsOnlyActiveMachines_whenActiveOnlyTrue() throws Exception {
        MachineEntity active = buildMachine("Active Press", true);
        MachineEntity inactive = buildMachine("Inactive Press", false);
        machineRepository.save(active);
        machineRepository.save(inactive);

        mockMvc.perform(get("/api/v1/machines?activeOnly=true")
                        .with(jwt().jwt(b -> b.subject(UUID.randomUUID().toString())
                                .claim("preferred_username", "operator1")
                                .claim("realm_access", java.util.Map.of("roles", java.util.List.of("OPERATOR")))))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name", is("Active Press")))
                .andExpect(jsonPath("$.data[0].isActive", is(true)))
                .andExpect(jsonPath("$.page").exists());
    }

    @Test
    void listMachines_returnsAllMachines_whenActiveOnlyFalse() throws Exception {
        machineRepository.save(buildMachine("Active Press", true));
        machineRepository.save(buildMachine("Inactive Press", false));

        mockMvc.perform(get("/api/v1/machines?activeOnly=false")
                        .with(jwt().jwt(b -> b.subject(UUID.randomUUID().toString())
                                .claim("preferred_username", "supervisor1")
                                .claim("realm_access", java.util.Map.of("roles", java.util.List.of("SUPERVISOR")))))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    @Test
    void listMachines_returns401_whenNoJwt() throws Exception {
        mockMvc.perform(get("/api/v1/machines")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listMachines_defaultsToActiveOnly_whenParamOmitted() throws Exception {
        machineRepository.save(buildMachine("Active1", true));
        machineRepository.save(buildMachine("Inactive1", false));

        mockMvc.perform(get("/api/v1/machines")
                        .with(jwt().jwt(b -> b.subject(UUID.randomUUID().toString())
                                .claim("preferred_username", "technician1")
                                .claim("realm_access", java.util.Map.of("roles", java.util.List.of("TECHNICIAN")))))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].isActive", is(true)));
    }

    private MachineEntity buildMachine(String name, boolean active) {
        MachineEntity e = new MachineEntity();
        e.setMachineId(UUID.randomUUID());
        e.setName(name);
        e.setActive(active);
        e.setCreatedAt(Instant.now());
        e.setUpdatedAt(Instant.now());
        return e;
    }
}
