package com.example.web;

import com.example.contract.MachineListResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/machines")
@Validated
public class MachineController {

    private final MachineService machineService;

    public MachineController(MachineService machineService) {
        this.machineService = machineService;
    }

    @GetMapping
    public ResponseEntity<MachineListResponse> listMachines(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "50") @Min(1) @Max(200) int limit,
            @RequestParam(required = false, defaultValue = "true") boolean activeOnly,
            Authentication authentication) {

        boolean isSupervisor = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_SUPERVISOR"::equals);

        // Non-supervisors always see active machines only
        boolean effectiveActiveOnly = isSupervisor ? activeOnly : true;

        MachineListResponse response = machineService.listMachines(
                effectiveActiveOnly, cursor, limit, isSupervisor);
        return ResponseEntity.ok(response);
    }
}
