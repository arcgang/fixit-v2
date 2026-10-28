# fixit-v2

FixIt v2 — every component of the architecture

| Component | Path | Technology | Seed |
|---|---|---|---|
| fixit-react-spa | `apps/fixit-react-spa` | React 18, TypeScript, Vite, AWS CloudFront, Amazon S3 | React |
| fixit-spring-boot-api | `services/fixit-spring-boot-api` | Java 21, Spring Boot 3.x, Spring Security, Spring Data JPA, Micrometer, Flyway | Java (Spring Boot) |
| keycloak-idp | `services/keycloak-idp` | Keycloak 24.x, PostgreSQL (Aurora — keycloak schema), Kubernetes (EKS) | blank |
| prometheus-grafana | `services/prometheus-grafana` | Prometheus 2.x, Grafana 10.x, Micrometer (Spring Boot), Amazon CloudWatch Metrics, Amazon SNS | Java (Spring Boot) |
| aws-api-gateway | `platform/aws-api-gateway` | AWS API Gateway HTTP API, JWT Authoriser, AWS WAF (via CloudFront) | blank |
| aurora-postgresql | `platform/aurora-postgresql` | Amazon Aurora PostgreSQL 15, AWS KMS CMK (fixit-aurora-cmk), Cross-region read replica (eu-central-1) | blank |
| aws-secrets-manager | `platform/aws-secrets-manager` | AWS Secrets Manager, AWS IAM IRSA, AWS KMS | blank |
| cloudwatch-logs | `platform/cloudwatch-logs` | Amazon CloudWatch Logs, Amazon S3 (log archive), AWS Glacier, AWS KMS SSE-KMS | blank |

Part of the **fixit-v2** estate (mono layout). Contracts: `contracts/` in this repository.

Stacks: React, Java (Spring Boot).
