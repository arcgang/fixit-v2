# aws-secrets-manager

**Technology:** AWS Secrets Manager, AWS IAM IRSA, AWS KMS

**LLD component:** `cmp:aws-secrets-manager`

Centralised secrets store for Aurora connection string, Keycloak client secret, and Phase 2 SMTP credentials. Automatic rotation ≤90 days. Accessed by Spring Boot API and Keycloak via IRSA (no static credentials).
