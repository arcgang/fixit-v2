# prometheus-grafana

LLD component `cmp:prometheus-grafana` · Prometheus 2.x, Grafana 10.x, Micrometer (Spring Boot), Amazon CloudWatch Metrics, Amazon SNS · seeded from Java (Spring Boot)

Platform observability: Prometheus scrapes Spring Boot Micrometer and Keycloak metrics; Grafana provides operational dashboards for p95 latency, error rates, SSE connection counts, SLA breach rates, and EKS resource utilisation. Alerting via CloudWatch Alarms + SNS.

## Stories

- `EN-prometheus-grafana`
- `US-001`
- `US-003`
- `US-005`
- `US-006`
- `US-008`

## Endpoints

- `GET /healthz`
- `GET /api/v1/slo`
- `GET /api/v1/alerts`
- `GET /api/v1/dashboards`
- `GET /api/v1/slo/{sloId}`
- `POST /api/v1/alerts/webhook`
- `GET /api/v1/scrape-targets`
- `GET /api/v1/alerts/{alertId}`
- `GET /api/v1/dashboards/{dashboardId}`

## Entities

- `ent:alert-record`
- `ent:alert-state-transition`
- `ent:dashboard-metadata`
- `ent:scrape-target-state`
- `ent:slo-definition`
- `ent:slo-evaluation`

## Acceptance criteria

- AC-4bb8c0f4 (US-001): Given I am logged in as an Operator, When I open the new request form, Then I see a machine selector populated only with active machines from the master list, a free-text description field, and a priority selector with values Critical, Medium, and Low.
- AC-a6372e9c (US-001): Given I have selected a machine, entered a description, and chosen a priority, When I submit the form, Then the request is created with status Open, a creation timestamp is recorded, and I am shown an on-screen confirmation with the request reference number.
- AC-91d7680f (US-001): Given I submit the form with the machine selector empty, the description empty, or no priority selected, When I attempt to submit, Then the form is not submitted and each missing field is highlighted with an error message.
- AC-84645e7d (US-001): Given the machine list contains deactivated machines, When I open the machine selector, Then deactivated machines do not appear as selectable options.
- AC-af6de81d (US-003): Given I am logged in as a Technician, When I open the request queue, Then I see all requests with status Open, sorted with Critical at the top, followed by Medium, then Low; within the same priority, requests are sorted by creation timestamp ascending.
- AC-b0f38811 (US-003): Given multiple Critical requests exist, When I view the queue, Then they appear before any Medium or Low requests regardless of when they were created.
- AC-c2c2c5df (US-003): Given a new Critical request is submitted while I am viewing the queue, When the queue refreshes (within 60 seconds or on manual refresh), Then the new request appears in the correct position.
- AC-eed80de8 (US-003): Given I am logged in as an Operator, When I attempt to access the Technician queue view, Then I am denied access and see an authorisation error.
- AC-659dc640 (US-005): Given I am logged in as a Technician and I am the assignee of a request with status Open, When I click 'Start work', Then the request status changes to In Progress and the transition timestamp is recorded.
- AC-41284728 (US-005): Given a request is In Progress, When any user views the request, Then they see the In Progress status and the timestamp at which work started.
- AC-2dfbd88a (US-005): Given I am not the assignee of a request, When I view that request, Then the 'Start work' action is not available to me.
- AC-38a54266 (US-005): Given I am logged in as an Operator or Supervisor, When I view any request, Then the 'Start work' action is not available to me.
- AC-6b91db1d (US-006): Given I am logged in as a Technician and I am the assignee of a request with status In Progress, When I click 'Resolve', Then I am presented with a resolution note field that is mandatory before the action can be confirmed.
- AC-3cb6269a (US-006): Given I attempt to resolve a request with an empty resolution note field, When I click confirm, Then the request is not resolved and an error message states that a resolution note is required.
- AC-a727ab1b (US-006): Given I enter a resolution note and confirm, When the resolution is saved, Then the request status changes to Resolved, the resolution note is stored, and the resolution timestamp is recorded.
- AC-79943d9e (US-006): Given a request is Resolved, When any user views the request, Then they can read the resolution note and see the resolved timestamp.
- AC-dd8a9cfd (US-006): Given I am logged in as an Operator or Supervisor, When I view any request, Then the 'Resolve' action is not available to me.
- AC-46c40242 (US-008): Given I am logged in as a Supervisor, When I open the dashboard, Then I see three count tiles: Open, In Progress, and Resolved, each showing the current total for that status.
- AC-db58309e (US-008): Given at least one request has been in Open status for more than 24 hours, When I view the dashboard, Then those requests are listed in a dedicated 'Overdue (>24 h)' section showing machine name, priority, fault description summary, and time elapsed since creation.
- AC-f57841ce (US-008): Given all open requests are less than 24 hours old, When I view the dashboard, Then the overdue section shows an empty state message confirming no requests are currently overdue.
- AC-a9aa9905 (US-008): Given a request transitions from Open to In Progress, When the dashboard refreshes (within 60 seconds or on manual refresh), Then the Open count decreases by one and the In Progress count increases by one.
- AC-1ce41875 (US-008): Given I am logged in as an Operator or Technician, When I attempt to access the Supervisor dashboard URL, Then I am denied access and see an authorisation error.

## Non-functional requirements

- availability: 99.5% uptime measured monthly during defined shift hours (excluding pre-announced maintenance windows of ≤ 2 hours per month)
- latency: 95th-percentile page load time ≤ 3 seconds under a concurrent load of 50 users on the shop-floor Wi-Fi network
- scalability: Supports 100 concurrent authenticated users with no increase in 95th-percentile response time beyond the NFR-01 threshold
- performance: 95th-percentile page load time ≤ 3 seconds under a concurrent load of 50 users on the shop-floor Wi-Fi network
- performance: Status change visible to all active sessions within 60 seconds of the transition being saved, without requiring a manual page reload
- availability: 99.5% uptime measured monthly during defined shift hours (excluding pre-announced maintenance windows of ≤ 2 hours per month)
- reliability: Zero silent data-loss events on request submission; 100% of failed submissions return an error message within 5 seconds
- security: 100% of HTTP traffic served over TLS 1.2 or higher; no unencrypted HTTP endpoints exposed
- security: Zero successful unauthorised actions in penetration testing; every role-restricted API endpoint returns HTTP 403 when called by an unauthorised role
- data_management: Zero records of timestamp or resolution-note modification in the audit log; any attempt to modify returns HTTP 403
- data_management: All request records retained for a minimum of 7 years from the date of resolution; records must be exportable before deletion
- usability: New Operator completes first successful request submission in ≤ 2 minutes in usability testing with no prior system exposure
- accessibility: WCAG 2.1 Level AA conformance across all pages, verified by automated scan and manual review
- scalability: Supports 100 concurrent authenticated users with no increase in 95th-percentile response time beyond the NFR-01 threshold
- observability: 100% of status transitions and notification dispatch events logged with request ID, timestamp, actor, and outcome; logs retained for 90 days
- integration: System displays a clear service-unavailable message within 10 seconds of IdP timeout; no partial or corrupted session is created
- maintainability: SLA thresholds and shift-hours configuration are stored in application configuration, changeable via an admin interface or configuration file, with changes taking effect within 5 minutes without a restart
- privacy: No personal data fields beyond display name, role, and system-generated user ID are stored; data minimisation reviewed at each release

## Settled decisions

- **Should prometheus-grafana own any business-domain tables (e.g. maintenancerequest, machine)?** → No. prometheus-grafana owns only observability-schema tables: alert_record, alert_state_transition, slo_definition, slo_evaluation, scrape_target_state, dashboard_metadata. All business data remains in fixit-spring-boot-api's tables. (`observability-owns-no-business-tables`)
- **What scrape interval should Prometheus use for the Spring Boot Micrometer endpoint?** → 15-second scrape interval for fixit-spring-boot-api and Keycloak metrics endpoints. (`prometheus-scrape-interval`)
- **How do CloudWatch Alarms reach the Grafana/Prometheus alerting pipeline?** → CloudWatch Alarms publish to an Amazon SNS topic. SNS delivers via HTTPS POST to POST /api/v1/alerts/webhook on the observability service. The webhook persists the alert to alert_record and alert_state_transition. (`cloudwatch-sns-alert-webhook`)
- **Can alert records be updated or deleted?** → alert_record rows are effectively immutable after INSERT. State transitions are recorded as new rows in alert_state_transition. The updated_at column on alert_record is set only when resolved_at is populated on resolution. (`alert-record-immutability`)
- **Which SLOs are tracked by the observability stack?** → Five SLOs: (1) API availability ≥99.5% over 30-day rolling window; (2) p95 HTTP latency ≤3s; (3) SSE connection count (active connections gauge); (4) SLA breach rate (Critical requests not resolved within 1h); (5) EKS pod CPU/memory utilisation below 80%. (`slo-definitions`)
- **How is the observability API secured?** → All non-health endpoints require a Bearer JWT issued by Keycloak. The Grafana service account and any CI/CD tooling must obtain a token from the fixit Keycloak realm. The /healthz probe is unauthenticated for Kubernetes liveness checks. (`grafana-auth-bearer-jwt`)
- **What pagination style is used for list endpoints?** → Cursor-based pagination (?cursor=&limit=) on all list endpoints: /api/v1/alerts, /api/v1/dashboards, /api/v1/slo, /api/v1/scrape-targets. Cursor is an opaque base64-encoded timestamp+id tuple. (`cursor-pagination-for-alerts`)
- **Should observability tables share the fixit application schema or use a dedicated schema?** → A dedicated 'observability' schema within the same Aurora PostgreSQL cluster. No cross-schema foreign keys to business tables. (`observability-schema-isolation`)
- **How long are alert records retained?** → 90 days, consistent with the NFR requiring logs retained for 90 days. A scheduled PostgreSQL job (pg_cron or EKS CronJob) deletes alert_record rows where created_at < now() - interval '90 days'. (`alert-retention-90-days`)
- **Which custom Micrometer metrics must fixit-spring-boot-api emit for the observability dashboards?** → fixit_requests_created_total{priority} (counter), fixit_request_open_duration_seconds (histogram), fixit_request_resolution_duration_seconds (histogram), fixit_requests_open_gt24h_total (gauge), fixit_sla_breach_total{priority} (counter), sse_connections_active (gauge). Standard Spring Boot http_server_requests_seconds and JVM metrics are also scraped. (`micrometer-custom-metrics`)
- **Should the observability stack use an event bus or message queue to receive metrics or alerts?** → No. The technology decision mandates messaging: none. All integration is synchronous: Prometheus pull-scrape for metrics, SNS→HTTPS webhook for CloudWatch Alarms. (`no-messaging-broker`)
- **How are Grafana dashboards provisioned and kept in sync with dashboard_metadata?** → Grafana dashboards are provisioned via ConfigMap-mounted JSON files (Grafana provisioning API). On startup, a sidecar init container reads the provisioned dashboard UIDs and upserts rows into observability.dashboard_metadata. The GET /api/v1/dashboards endpoint reads from this table. (`grafana-dashboard-provisioning`)

## Contract

The endpoints, schemas, tables and events above are defined in `contracts/` in this repository. Implement them as written; a change to the contract is a change to the design.
