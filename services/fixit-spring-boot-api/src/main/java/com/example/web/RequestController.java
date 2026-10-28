package com.example.web;

import com.example.contract.CreateRequestBody;
import com.example.contract.MaintenanceRequest;
import com.example.security.CallerPrincipal;
import com.example.service.RequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(final RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ResponseEntity<MaintenanceRequest> createRequest(
            @Valid @RequestBody CreateRequestRequest body,
            @AuthenticationPrincipal CallerPrincipal caller) {

        CreateRequestBody serviceBody = new CreateRequestBody(body.priority(), body.machineId(), body.description());
        MaintenanceRequest result = requestService.createRequest(serviceBody, caller.userId(), caller.displayName());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
