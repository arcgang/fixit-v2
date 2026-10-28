package com.example.web;

import com.example.contract.CreateRequestBody;
import com.example.contract.ErrorBody;
import com.example.contract.MaintenanceRequest;
import com.example.service.RequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(final RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    @Secured("ROLE_OPERATOR")
    public ResponseEntity<?> createRequest(
            @RequestBody CreateRequestBody body,
            Authentication authentication) {

        List<Map<String, Object>> validationErrors = validate(body);
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorBody("VALIDATION_ERROR", validationErrors, "Request validation failed", UUID.randomUUID()));
        }

        UUID actorUserId = extractUserId(authentication);
        String actorDisplayName = extractDisplayName(authentication);

        MaintenanceRequest result = requestService.createRequest(
            body.machineId(),
            body.description(),
            body.priority(),
            actorUserId,
            actorDisplayName
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    private List<Map<String, Object>> validate(CreateRequestBody body) {
        List<Map<String, Object>> errors = new ArrayList<>();
        if (body == null || body.machineId() == null) {
            errors.add(Map.of("field", "machineId", "message", "must not be null"));
        }
        if (body == null || body.description() == null || body.description().isBlank()) {
            errors.add(Map.of("field", "description", "message", "must not be blank"));
        }
        if (body == null || body.priority() == null) {
            errors.add(Map.of("field", "priority", "message", "must not be null"));
        }
        return errors;
    }

    private UUID extractUserId(Authentication authentication) {
        if (authentication == null) throw new IllegalStateException("Authentication must not be null on a secured endpoint");
        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException e) {
            return UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        }
    }

    private String extractDisplayName(Authentication authentication) {
        if (authentication == null) throw new IllegalStateException("Authentication must not be null on a secured endpoint");
        return authentication.getName();
    }
}
