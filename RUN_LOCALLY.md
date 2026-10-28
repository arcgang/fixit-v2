# Run fixit-v2 locally

Prerequisites: Docker Desktop, Node 20+, Python 3.11+, JDK 21 + Maven.

```sh
git clone https://github.com/arcgang/fixit-v2.git && cd fixit-v2
make up        # the local platform: postgres, minio
make db-schema # the LLD's tables, in postgres
make seed      # synthetic rows to click through
make install   # every component's dependencies
make test      # every component's tests
make help      # everything else
```

## Platform (docker compose)

| Service | Stands in for | Connect with |
|---|---|---|
| postgres — Postgres 16 | aurora-postgresql | `DATABASE_URL=postgresql://postgres:local@localhost:5432/app` |
| minio — MinIO, S3-compatible | cloudwatch-logs | `S3_ENDPOINT=http://localhost:9000`<br>`AWS_ACCESS_KEY_ID=local`<br>`AWS_SECRET_ACCESS_KEY=localpassword` |

The connection settings are in `.env.example` (copied to `.env` by `make up`).

## Database

`db/schema.sql` is the LLD's Postgres DDL — the contract the services implement. `make db-schema` loads it into `postgres` so the app runs before any service ships migrations; once one does, run its migrations instead and keep the two in step. `db/seed.sql` holds synthetic rows (a few per table, parents first, no real personal data) generated from that DDL: `make seed` loads them, `make db-reset` starts over. Never load it outside a laptop.

### Not run locally

No faithful local stand-in — stub or mock these in tests and point at a shared environment for integration:

- aws-api-gateway (AWS API Gateway HTTP API, JWT Authoriser, AWS WAF (via CloudFront))
- aws-secrets-manager (AWS Secrets Manager, AWS IAM IRSA, AWS KMS)

## Components

| Component | Path | Run | Notes |
|---|---|---|---|
| fixit-react-spa | `apps/fixit-react-spa` | `make dev-fixit-react-spa` | `make test-fixit-react-spa` |
| fixit-spring-boot-api | `services/fixit-spring-boot-api` | `make dev-fixit-spring-boot-api` | `make test-fixit-spring-boot-api` |
| keycloak-idp | `services/keycloak-idp` | — | no toolchain yet — the first task for this component lays it down |
| prometheus-grafana | `services/prometheus-grafana` | `make dev-prometheus-grafana` | `make test-prometheus-grafana` |
