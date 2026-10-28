# prometheus-grafana

**Technology:** Prometheus, Grafana, EKS (cluster-internal)

**LLD component:** `cmp:prometheus-grafana`

Platform observability — Prometheus scrapes Spring Boot Actuator metrics; Grafana dashboards for p95 latency, availability, SSE connection count, error rates; alerting for p95 >3 s and availability <99.5%
