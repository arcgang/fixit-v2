# cloudwatch-logs

**Technology:** Amazon CloudWatch Logs, Amazon S3 (log archive), AWS Glacier, AWS KMS SSE-KMS

**LLD component:** `cmp:cloudwatch-logs`

Centralised structured log aggregation for application logs and immutable audit log entries (request ID, timestamp, actor, outcome). 90-day operational retention; 7-year request record retention via S3 export to Glacier.
