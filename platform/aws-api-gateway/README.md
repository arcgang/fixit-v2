# aws-api-gateway

**Technology:** AWS API Gateway HTTP API, JWT Authoriser, AWS WAF (via CloudFront)

**LLD component:** `cmp:aws-api-gateway`

Single ingress mediator for all client-to-server REST and SSE traffic. Enforces TLS 1.2+, JWT Bearer validation (Keycloak JWKS), rate limiting, and quota policies. Routes to Spring Boot API internal ALB.
