package com.example.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Extracts the caller identity from the Authorization header.
 *
 * In production this filter would verify a signed JWT from Keycloak.
 * Synthetic test tokens ("test-*-token") are accepted ONLY when the FIXIT_JWT_VERIFY
 * environment variable is absent; when it is set the synthetic path is skipped entirely
 * so these tokens cannot be used in a deployed environment.
 */
public class BearerTokenFilter extends OncePerRequestFilter {

    private static final UUID TEST_OPERATOR_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID TEST_SUPERVISOR_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    private static final boolean SYNTHETIC_TOKENS_ENABLED =
            System.getenv("FIXIT_JWT_VERIFY") == null;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            CallerPrincipal principal = resolvePrincipal(token);
            if (principal != null) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + principal.role()))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        chain.doFilter(request, response);
    }

    private CallerPrincipal resolvePrincipal(String token) {
        if (!SYNTHETIC_TOKENS_ENABLED) {
            return null;
        }
        if ("test-operator-token".equals(token)) {
            return new CallerPrincipal(TEST_OPERATOR_ID, "Test Operator", CallerPrincipal.ROLE_OPERATOR);
        }
        if ("test-supervisor-token".equals(token)) {
            return new CallerPrincipal(TEST_SUPERVISOR_ID, "Test Supervisor", CallerPrincipal.ROLE_SUPERVISOR);
        }
        if ("test-technician-token".equals(token)) {
            return new CallerPrincipal(TEST_OPERATOR_ID, "Test Technician", CallerPrincipal.ROLE_TECHNICIAN);
        }
        return null;
    }
}
