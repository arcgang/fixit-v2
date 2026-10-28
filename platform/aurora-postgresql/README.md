# aurora-postgresql

**Technology:** Amazon Aurora PostgreSQL 15, AWS KMS CMK (fixit-aurora-cmk), Cross-region read replica (eu-central-1)

**LLD component:** `cmp:aurora-postgresql`

Sole relational data store for all FixIt data: maintenance requests, machines (soft-delete), comments, status transition audit records, SLA outcomes, system configuration, and Keycloak realm data. Immutable write pattern for state transitions and resolution notes.
