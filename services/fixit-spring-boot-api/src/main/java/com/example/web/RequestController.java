package com.example.web;

import com.example.contract.CreateRequestBody;
import com.example.contract.MaintenanceRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('OPERATOR')")
    public MaintenanceRequest createRequest(
            @RequestBody CreateRequestBody body,
            @AuthenticationPrincipal Jwt jwt) {
        UUID actorId = UUID.fromString(jwt.getSubject());
        String displayName = jwt.getClaimAsString("preferred_username");
        return requestService.createRequest(body, actorId, displayName);
    }
}
