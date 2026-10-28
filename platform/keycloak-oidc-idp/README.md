# keycloak-oidc-idp

**Technology:** Keycloak 24+, OIDC, SAML 2.0, EKS Pod (HA)

**LLD component:** `cmp:keycloak-oidc-idp`

OIDC identity provider — issues JWT tokens with role claims (Operator/Technician/Supervisor), provides JWKS endpoint, enforces MFA, token TTL ≤1 hour. ARB exception required against dec-004.
