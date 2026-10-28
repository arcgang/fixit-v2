package com.example.web;

import com.example.contract.CursorPage;
import com.example.contract.Machine;
import com.example.contract.MachineListResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MachineController.class)
@Import(SecurityConfig.class)
class MachineControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    MachineService machineService;

    @MockBean
    JwtDecoder jwtDecoder;

    private static final Instant FIXED = Instant.parse("2030-01-01T00:00:00Z");

    private Machine activeMachine(String name) {
        return new Machine(name, true, "Floor 1", FIXED,
                UUID.randomUUID(), null, "ASSET-001", null);
    }

    private Machine inactiveMachine(String name) {
        return new Machine(name, false, "Floor 2", FIXED,
                UUID.randomUUID(), FIXED, "ASSET-002", UUID.randomUUID());
    }

    @Test
    void getWithNoToken_returns401() throws Exception {
        mvc.perform(get("/machines").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAsOperator_defaultParams_returnsOnlyActiveMachines() throws Exception {
        Machine m1 = activeMachine("Press A");
        MachineListResponse response = new MachineListResponse(List.of(m1), new CursorPage(50L, null));
        when(machineService.listMachines(eq(true), isNull(), eq(50)))
                .thenReturn(response);

        mvc.perform(get("/machines")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERATOR")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name", is("Press A")))
                .andExpect(jsonPath("$.data[0].isActive", is(true)))
                .andExpect(jsonPath("$.page.next_cursor", nullValue()));
    }

    @Test
    void getAsOperator_activeOnlyFalse_stillReturnsOnlyActive() throws Exception {
        Machine m1 = activeMachine("Lathe B");
        MachineListResponse response = new MachineListResponse(List.of(m1), new CursorPage(50L, null));
        // Operator passes activeOnly=false but service is called with activeOnly=true because role enforcement
        when(machineService.listMachines(eq(true), isNull(), eq(50)))
                .thenReturn(response);

        mvc.perform(get("/machines?activeOnly=false")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERATOR")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].isActive", is(true)));
    }

    @Test
    void getAsSupervisor_activeOnlyFalse_returnsAllMachines() throws Exception {
        Machine active = activeMachine("Press A");
        Machine inactive = inactiveMachine("Old Drill");
        MachineListResponse response = new MachineListResponse(List.of(active, inactive), new CursorPage(50L, null));
        when(machineService.listMachines(eq(false), isNull(), eq(50)))
                .thenReturn(response);

        mvc.perform(get("/machines?activeOnly=false")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SUPERVISOR")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[1].isActive", is(false)));
    }

    @Test
    void getWithLimit2_threeActiveMachines_nextCursorIsNonNull() throws Exception {
        Machine m1 = activeMachine("M1");
        Machine m2 = activeMachine("M2");
        String encodedCursor = "dGVzdC1jdXJzb3I="; // base64("test-cursor")
        MachineListResponse response = new MachineListResponse(List.of(m1, m2), new CursorPage(2L, encodedCursor));
        when(machineService.listMachines(eq(true), isNull(), eq(2)))
                .thenReturn(response);

        mvc.perform(get("/machines?limit=2")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERATOR")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.page.next_cursor", is(encodedCursor)));
    }

    @Test
    void getWithLimit2_secondPage_nextCursorIsNull() throws Exception {
        Machine m3 = activeMachine("M3");
        String cursor = "dGVzdC1jdXJzb3I=";
        MachineListResponse response = new MachineListResponse(List.of(m3), new CursorPage(2L, null));
        when(machineService.listMachines(eq(true), eq(cursor), eq(2)))
                .thenReturn(response);

        mvc.perform(get("/machines?limit=2&cursor=" + cursor)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERATOR")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.page.next_cursor", nullValue()));
    }

    @Test
    void getWithLimit0_returns400() throws Exception {
        mvc.perform(get("/machines?limit=0")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERATOR")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWithLimit201_returns400() throws Exception {
        mvc.perform(get("/machines?limit=201")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERATOR")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
