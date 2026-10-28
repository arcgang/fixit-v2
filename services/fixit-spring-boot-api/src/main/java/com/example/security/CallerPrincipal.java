package com.example.security;

import java.util.UUID;

public record CallerPrincipal(UUID userId, String displayName, String role) {

    public static final String ROLE_OPERATOR = "OPERATOR";
    public static final String ROLE_TECHNICIAN = "TECHNICIAN";
    public static final String ROLE_SUPERVISOR = "SUPERVISOR";
}
