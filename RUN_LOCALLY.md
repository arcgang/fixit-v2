# Run fixit-v2 locally

Prerequisites: Docker Desktop, Node 20+, Python 3.11+, JDK 21 + Maven.

```sh
git clone https://github.com/arcgang/fixit-v2.git && cd fixit-v2
make up        # the local platform: oidc, postgres, minio, otel-lgtm
make db-schema # the LLD's tables, in postgres
make seed      # synthetic rows to click through
make install   # every component's dependencies
make test      # every component's tests
make help      # everything else
```

## Platform (docker compose)

| Service | Stands in for | Connect with |
|---|---|---|
| oidc — mock OAuth2/OIDC server standing in for the identity provider — any login succeeds | keycloak-oidc-idp | `OIDC_ISSUER_URL=http://localhost:8090/default`<br>`OIDC_CLIENT_ID=local`<br>`OIDC_CLIENT_SECRET=local` |
| postgres — Postgres 16 | amazon-aurora-postgresql | `DATABASE_URL=postgresql://postgres:local@localhost:5432/app` |
| minio — MinIO, S3-compatible | aws-cloudwatch-logs, amazon-s3 | `S3_ENDPOINT=http://localhost:9000`<br>`AWS_ACCESS_KEY_ID=local`<br>`AWS_SECRET_ACCESS_KEY=localpassword` |
| otel-lgtm — Grafana + Prometheus + Tempo + Loki with an OTLP endpoint, in one container | prometheus-grafana | `OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4318` |

The connection settings are in `.env.example` (copied to `.env` by `make up`).

## Database

`db/schema.sql` is the LLD's Postgres DDL — the contract the services implement. `make db-schema` loads it into `postgres` so the app runs before any service ships migrations; once one does, run its migrations instead and keep the two in step. `db/seed.sql` holds synthetic rows (a few per table, parents first, no real personal data) generated from that DDL: `make seed` loads them, `make db-reset` starts over. Never load it outside a laptop.

### Not run locally

No faithful local stand-in — stub or mock these in tests and point at a shared environment for integration:

- aws-api-gateway (AWS API Gateway HTTP API, JWT Authoriser)
- aws-secrets-manager (AWS Secrets Manager, AWS KMS)
- aws-kms (AWS KMS)

## Components

| Component | Path | Run | Notes |
|---|---|---|---|
| fixit-web-frontend | `apps/fixit-web-frontend` | `make dev-fixit-web-frontend` | `make test-fixit-web-frontend` |
| fixit-api-service | `services/fixit-api-service` | `make dev-fixit-api-service` | `make test-fixit-api-service` |
| aws-alb | `services/aws-alb` | — | no toolchain yet — the first task for this component lays it down |
| smtp-relay-phase2 | `services/smtp-relay-phase2` | — | no toolchain yet — the first task for this component lays it down |
