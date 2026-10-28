package com.example.web;

import com.example.contract.Machine;
import com.example.contract.MachineListResponse;
import com.example.contract.CursorPage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MachineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    private UUID insertMachine(String name, boolean isActive) {
        UUID id = UUID.randomUUID();
        jdbc.update(
            "INSERT INTO machine (machine_id, name, is_active, created_at, updated_at) VALUES (?, ?, ?, now(), now())",
            id, name, isActive
        );
        return id;
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void listMachines_activeOnly_returnsOnlyActiveMachines() throws Exception {
        UUID activeId = insertMachine("Lathe-01", true);
        insertMachine("Drill-01", false);

        mockMvc.perform(get("/machines").param("activeOnly", "true"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(1)))
            .andExpect(jsonPath("$.data[0].machineId", is(activeId.toString())))
            .andExpect(jsonPath("$.data[0].name", is("Lathe-01")))
            .andExpect(jsonPath("$.data[0].isActive", is(true)))
            .andExpect(jsonPath("$.page.limit", is(50)))
            .andExpect(jsonPath("$.page.next_cursor").doesNotExist());
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void listMachines_defaultActiveOnly_excludesDeactivatedMachines() throws Exception {
        insertMachine("Active-Machine", true);
        insertMachine("Deactivated-Machine", false);

        mockMvc.perform(get("/machines"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(1)))
            .andExpect(jsonPath("$.data[0].name", is("Active-Machine")));
    }

    @Test
    @WithMockUser(roles = "SUPERVISOR")
    void listMachines_supervisorWithActiveOnlyFalse_returnsAllMachines() throws Exception {
        insertMachine("Active-Machine", true);
        insertMachine("Deactivated-Machine", false);

        mockMvc.perform(get("/machines").param("activeOnly", "false"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(2)));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void listMachines_operatorCannotSeeInactiveMachinesWithActiveOnlyFalse() throws Exception {
        insertMachine("Active-Machine", true);
        insertMachine("Deactivated-Machine", false);

        // Operator requesting activeOnly=false is treated as activeOnly=true (forced)
        mockMvc.perform(get("/machines").param("activeOnly", "false"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(1)))
            .andExpect(jsonPath("$.data[0].name", is("Active-Machine")));
    }

    @Test
    void listMachines_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/machines"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void listMachines_paginationLimit_respectsLimitParam() throws Exception {
        for (int i = 0; i < 5; i++) {
            insertMachine("Machine-" + i, true);
        }

        String response = mockMvc.perform(get("/machines").param("limit", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(2)))
            .andExpect(jsonPath("$.page.limit", is(2)))
            .andReturn().getResponse().getContentAsString();

        JsonNode json = new ObjectMapper().readTree(response);
        // The cursor must decode to the machineId of the last item on the returned page
        UUID lastOnPage = UUID.fromString(json.get("data").get(1).get("machineId").asText());
        String encodedCursor = json.get("page").get("next_cursor").asText();
        UUID decodedCursorId = UUID.fromString(new String(Base64.getUrlDecoder().decode(encodedCursor)));
        assertEquals(lastOnPage, decodedCursorId, "next_cursor must decode to the UUID of the last item on the first page");
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void listMachines_cursorPagination_returnsNextPage() throws Exception {
        for (int i = 0; i < 3; i++) {
            insertMachine("Machine-" + i, true);
        }

        // Get first page
        String firstPageResponse = mockMvc.perform(get("/machines").param("limit", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(2)))
            .andReturn().getResponse().getContentAsString();

        String cursor = new ObjectMapper().readTree(firstPageResponse).get("page").get("next_cursor").asText();

        // Get second page with cursor
        mockMvc.perform(get("/machines").param("limit", "2").param("cursor", cursor))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void listMachines_responseIncludesRequiredContractFields() throws Exception {
        UUID id = UUID.randomUUID();
        jdbc.update(
            "INSERT INTO machine (machine_id, name, is_active, location, asset_reference, created_at, updated_at) "
            + "VALUES (?, ?, ?, ?, ?, now(), now())",
            id, "Press-01", true, "Building A", "ASSET-001"
        );

        mockMvc.perform(get("/machines"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].machineId", is(id.toString())))
            .andExpect(jsonPath("$.data[0].name", is("Press-01")))
            .andExpect(jsonPath("$.data[0].isActive", is(true)))
            .andExpect(jsonPath("$.data[0].location", is("Building A")))
            .andExpect(jsonPath("$.data[0].assetReference", is("ASSET-001")))
            .andExpect(jsonPath("$.data[0].createdAt").isNotEmpty());
    }
}
