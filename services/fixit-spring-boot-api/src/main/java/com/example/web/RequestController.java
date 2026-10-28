package com.example.web;

import com.example.contract.CreateRequestBody;
import com.example.contract.MaintenanceRequest;
import com.example.service.MaintenanceRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/requests")
public class RequestController {

    private final MaintenanceRequestService requestService;

    public RequestController(final MaintenanceRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<MaintenanceRequest> createRequest(
            @Valid @RequestBody CreateRequestBodyValidated body,
            @AuthenticationPrincipal Jwt jwt) {

        UUID actorUserId = UUID.fromString(jwt.getSubject());
        String actorDisplayName = jwt.getClaimAsString("preferred_username");
        if (actorDisplayName == null || actorDisplayName.isBlank()) {
            actorDisplayName = actorUserId.toString();
        }

        CreateRequestBody contractBody = new CreateRequestBody(body.priority(), body.machineId(), body.description());
        MaintenanceRequest result = requestService.createRequest(contractBody, actorUserId, actorDisplayName);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
