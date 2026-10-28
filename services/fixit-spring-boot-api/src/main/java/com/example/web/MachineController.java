package com.example.web;

import com.example.contract.MachineListResponse;
import com.example.service.MachineService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/machines")
public class MachineController {

    private final MachineService machineService;

    public MachineController(final MachineService machineService) {
        this.machineService = machineService;
    }

    @GetMapping
    public ResponseEntity<MachineListResponse> listMachines(
            @RequestParam(defaultValue = "true") boolean activeOnly,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "50") int limit,
            Authentication authentication) {

        if (limit < 1 || limit > 200) {
            limit = Math.max(1, Math.min(limit, 200));
        }

        // Operators and Technicians can only see active machines regardless of request
        boolean effectiveActiveOnly = activeOnly;
        if (!hasSupervisorRole(authentication)) {
            effectiveActiveOnly = true;
        }

        MachineListResponse response = machineService.listMachines(effectiveActiveOnly, cursor, limit);
        return ResponseEntity.ok(response);
    }

    private boolean hasSupervisorRole(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPERVISOR"));
    }
}
