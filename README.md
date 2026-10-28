# fixit-v2

FixIt v2 — every component of the architecture

| Component | Path | Technology | Seed |
|---|---|---|---|
| fixit-web-frontend | `apps/fixit-web-frontend` | React 18, TypeScript, Nginx, EKS Pod | React |
| fixit-api-service | `services/fixit-api-service` | Java 21, Spring Boot 3, Spring Security, EKS Pod | Java (Spring Boot) |
| aws-alb | `services/aws-alb` | AWS ALB, AWS Certificate Manager | blank |
| smtp-relay-phase2 | `services/smtp-relay-phase2` | SMTP-TLS (STARTTLS port 587) or Webhook (HTTPS) | blank |
| aws-api-gateway | `platform/aws-api-gateway` | AWS API Gateway HTTP API, JWT Authoriser | blank |
| keycloak-oidc-idp | `platform/keycloak-oidc-idp` | Keycloak 24+, OIDC, SAML 2.0, EKS Pod (HA) | blank |
| amazon-aurora-postgresql | `platform/amazon-aurora-postgresql` | Amazon Aurora PostgreSQL 15, Multi-AZ, AWS KMS CMK | blank |
| aws-secrets-manager | `platform/aws-secrets-manager` | AWS Secrets Manager, AWS KMS | blank |
| aws-cloudwatch-logs | `platform/aws-cloudwatch-logs` | AWS CloudWatch Logs, Amazon S3 (archival), AWS KMS | blank |
| prometheus-grafana | `platform/prometheus-grafana` | Prometheus, Grafana, EKS (cluster-internal) | blank |
| aws-kms | `platform/aws-kms` | AWS KMS | blank |
| amazon-s3 | `platform/amazon-s3` | Amazon S3, S3 Lifecycle Rules, AWS KMS | blank |

Part of the **fixit-v2** estate (mono layout). Contracts: `contracts/` in this repository.

Stacks: React, Java (Spring Boot).
