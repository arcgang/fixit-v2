# amazon-aurora-postgresql

**Technology:** Amazon Aurora PostgreSQL 15, Multi-AZ, AWS KMS CMK

**LLD component:** `cmp:amazon-aurora-postgresql`

Primary persistent data store — all FixIt application data (requests, machines, users, comments, audit events, SLA config). Multi-AZ, KMS CMK encryption at rest, private subnets only, 7-year retention.
