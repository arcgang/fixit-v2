# fixit-spring-boot-api

LLD component `cmp:fixit-spring-boot-api` · Java 21, Spring Boot 3.x, Spring Security, Spring Data JPA, Micrometer, Flyway · seeded from Java (Spring Boot)

Core backend service implementing all business logic: request lifecycle state machine (Open→In Progress→Resolved), server-side RBAC, SLA calculation engine, SSE event broadcasting, commenting, machine register CRUD, downtime aggregation, system configuration management, and Data Export API.

## Stories

- `US-001`
- `US-002`
- `US-003`
- `US-004`
- `US-005`
- `US-006`
- `US-007`
- `US-008`
- `US-009`
- `US-010`
- `US-011`
- `US-012`
- `US-013`
- `US-014`
- `US-015`
- `US-016`
- `US-017`
- `US-018`
- `US-019`
- `US-020`

## Endpoints

- `GET /config`
- `PUT /config`
- `GET /machines`
- `POST /machines`
- `GET /requests`
- `POST /requests`
- `GET /dashboard`
- `GET /events/stream`
- `GET /export/requests`
- `GET /reports/downtime`
- `PATCH /machines/{machineId}`
- `GET /export/downtime-summary`
- `PATCH /requests/{requestId}/status`
- `GET /machines/{machineId}/history`
- `GET /requests/{requestId}/comments`
- `POST /requests/{requestId}/comments`
- `POST /api/v1/requests`
- `GET /api/v1/requests`
- `PATCH /api/v1/requests/{id}/status`
- `POST /api/v1/requests/{id}/comments`
- `GET /api/v1/requests/{id}/comments`
- `GET /api/v1/dashboard`
- `GET /api/v1/machines`
- `POST /api/v1/machines`
- `PATCH /api/v1/machines/{id}`
- `GET /api/v1/reports/downtime`
- `GET /api/v1/machines/{id}/history`
- `GET /api/v1/events/stream`
- `GET /api/v1/config`
- `PUT /api/v1/config`
- `GET /api/v1/export/requests`
- `GET /api/v1/export/downtime-summary`

## Entities

- `ent:config-change-log`
- `ent:machine`
- `ent:maintenance-request`
- `ent:request-comment`
- `ent:request-status-history`
- `ent:sla-record`
- `ent:system-config`
- `ent:maintenancerequest`
- `ent:requeststatushistory`
- `ent:requestcomment`
- `ent:slarecord`
- `ent:systemconfig`
- `ent:configchangelog`

## Acceptance criteria

- AC-4bb8c0f4 (US-001): Given I am logged in as an Operator, When I open the new request form, Then I see a machine selector populated only with active machines from the master list, a free-text description field, and a priority selector with values Critical, Medium, and Low.
- AC-a6372e9c (US-001): Given I have selected a machine, entered a description, and chosen a priority, When I submit the form, Then the request is created with status Open, a creation timestamp is recorded, and I am shown an on-screen confirmation with the request reference number.
- AC-91d7680f (US-001): Given I submit the form with the machine selector empty, the description empty, or no priority selected, When I attempt to submit, Then the form is not submitted and each missing field is highlighted with an error message.
- AC-84645e7d (US-001): Given the machine list contains deactivated machines, When I open the machine selector, Then deactivated machines do not appear as selectable options.
- AC-278220d5 (US-002): Given I am logged in as an Operator, When I navigate to 'My Requests', Then I see only the requests I have raised, each showing machine name, fault description summary, priority, current status, and creation timestamp.
- AC-def5d740 (US-002): Given one of my requests has been transitioned to In Progress or Resolved by a technician, When I view 'My Requests', Then the updated status is reflected without requiring a page reload within 60 seconds of the transition.
- AC-584f8d4b (US-002): Given I have raised no requests, When I navigate to 'My Requests', Then I see an empty state message explaining how to raise a first request.
- AC-66ce3990 (US-002): Given I am logged in as a Technician or Supervisor, When I attempt to access the Operator's 'My Requests' view for another user, Then I am denied access and see an authorisation error.
- AC-af6de81d (US-003): Given I am logged in as a Technician, When I open the request queue, Then I see all requests with status Open, sorted with Critical at the top, followed by Medium, then Low; within the same priority, requests are sorted by creation timestamp ascending.
- AC-b0f38811 (US-003): Given multiple Critical requests exist, When I view the queue, Then they appear before any Medium or Low requests regardless of when they were created.
- AC-c2c2c5df (US-003): Given a new Critical request is submitted while I am viewing the queue, When the queue refreshes (within 60 seconds or on manual refresh), Then the new request appears in the correct position.
- AC-eed80de8 (US-003): Given I am logged in as an Operator, When I attempt to access the Technician queue view, Then I am denied access and see an authorisation error.
- AC-f02593bf (US-004): Given I am logged in as a Technician and a request has status Open, When I click 'Assign to me', Then the request is assigned to my user account, the status remains Open (or transitions to In Progress per US-005 if I choose), and my name is visible to all users viewing the request.
- AC-81c73c11 (US-004): Given a request is already assigned to another technician, When I view that request, Then the 'Assign to me' action is not available and the current assignee's name is displayed.
- AC-722752e1 (US-004): Given I self-assign a request, When the assignment is saved, Then the assignment timestamp is recorded and visible on the request detail.
- AC-3bc3a625 (US-004): Given I am logged in as an Operator or Supervisor, When I view a request, Then the 'Assign to me' action is not available to me.
- AC-659dc640 (US-005): Given I am logged in as a Technician and I am the assignee of a request with status Open, When I click 'Start work', Then the request status changes to In Progress and the transition timestamp is recorded.
- AC-41284728 (US-005): Given a request is In Progress, When any user views the request, Then they see the In Progress status and the timestamp at which work started.
- AC-2dfbd88a (US-005): Given I am not the assignee of a request, When I view that request, Then the 'Start work' action is not available to me.
- AC-38a54266 (US-005): Given I am logged in as an Operator or Supervisor, When I view any request, Then the 'Start work' action is not available to me.
- AC-6b91db1d (US-006): Given I am logged in as a Technician and I am the assignee of a request with status In Progress, When I click 'Resolve', Then I am presented with a resolution note field that is mandatory before the action can be confirmed.
- AC-3cb6269a (US-006): Given I attempt to resolve a request with an empty resolution note field, When I click confirm, Then the request is not resolved and an error message states that a resolution note is required.
- AC-a727ab1b (US-006): Given I enter a resolution note and confirm, When the resolution is saved, Then the request status changes to Resolved, the resolution note is stored, and the resolution timestamp is recorded.
- AC-79943d9e (US-006): Given a request is Resolved, When any user views the request, Then they can read the resolution note and see the resolved timestamp.
- AC-dd8a9cfd (US-006): Given I am logged in as an Operator or Supervisor, When I view any request, Then the 'Resolve' action is not available to me.
- AC-92b22fad (US-007): Given I am logged in as any role, When I open a request detail page, Then I see a comment input field and a chronological list of all previous comments, each showing the author's name, role, comment text, and timestamp.
- AC-bcec04af (US-007): Given I type a comment and submit it, When the comment is saved, Then it appears immediately at the bottom of the comment history with my name, role, and the current timestamp.
- AC-472e9681 (US-007): Given I submit an empty comment, When I click add, Then the comment is not saved and an error message is shown.
- AC-5a8d0512 (US-007): Given a request has no comments, When I view the comment history, Then I see an empty state message indicating no comments have been added yet.
- AC-46c40242 (US-008): Given I am logged in as a Supervisor, When I open the dashboard, Then I see three count tiles: Open, In Progress, and Resolved, each showing the current total for that status.
- AC-db58309e (US-008): Given at least one request has been in Open status for more than 24 hours, When I view the dashboard, Then those requests are listed in a dedicated 'Overdue (>24 h)' section showing machine name, priority, fault description summary, and time elapsed since creation.
- AC-f57841ce (US-008): Given all open requests are less than 24 hours old, When I view the dashboard, Then the overdue section shows an empty state message confirming no requests are currently overdue.
- AC-a9aa9905 (US-008): Given a request transitions from Open to In Progress, When the dashboard refreshes (within 60 seconds or on manual refresh), Then the Open count decreases by one and the In Progress count increases by one.
- AC-1ce41875 (US-008): Given I am logged in as an Operator or Technician, When I attempt to access the Supervisor dashboard URL, Then I am denied access and see an authorisation error.
- AC-bf020ce2 (US-009): Given I am logged in as a Supervisor, When I navigate to machine management and click 'Add machine', Then I see a form with fields for machine name (required), asset reference or location (optional), and a save action.
- AC-b26705c4 (US-009): Given I complete the required fields and save, When the machine is created, Then it appears in the machine list with status Active and is immediately available in the Operator's machine selector.
- AC-f6518108 (US-009): Given I attempt to save a machine with an empty name field, When I click save, Then the machine is not created and the name field is highlighted with an error.
- AC-7b4bdd2f (US-009): Given I am logged in as an Operator or Technician, When I attempt to access machine management, Then I am denied access and see an authorisation error.
- AC-f28b4f00 (US-010): Given I am logged in as a Supervisor, When I select an active machine and click 'Edit', Then I can modify the machine name and optional fields and save the changes.
- AC-b90176ef (US-010): Given I save an edit, When the change is persisted, Then the updated name is reflected immediately in the machine selector on the request submission form.
- AC-f8a0993d (US-010): Given I attempt to save an edit that leaves the machine name empty, When I click save, Then the change is not saved and an error is shown.
- AC-85d68e37 (US-010): Given a machine has open or in-progress requests, When I edit its name, Then existing requests retain a reference to the machine and display the updated name.
- AC-e928a740 (US-011): Given I am logged in as a Supervisor, When I select an active machine and click 'Deactivate', Then I am asked to confirm the action before it is applied.
- AC-a61ddfda (US-011): Given I confirm deactivation, When the machine is deactivated, Then it no longer appears in the Operator's machine selector for new requests.
- AC-1c93cafa (US-011): Given a deactivated machine has historical resolved requests, When I view those requests, Then they still reference the machine name and the full request history is preserved.
- AC-5781285e (US-011): Given a machine has open or in-progress requests, When I attempt to deactivate it, Then I am shown a warning listing the open requests and must explicitly confirm before deactivation proceeds.
- AC-0d4ac7e1 (US-012): Given a request exists with any priority, When any user views the request detail, Then the applicable SLA target resolution time is displayed alongside the priority label.
- AC-b7d8ba61 (US-012): Given a Critical request has been open for 45 minutes without being resolved, When the dashboard or request list is viewed, Then the request is visually flagged as 'At risk' (within 15 minutes of SLA breach).
- AC-3b88cc43 (US-012): Given a request has exceeded its SLA resolution time without being resolved, When any user views the request, Then it is visually flagged as 'SLA breached' with the elapsed overdue time shown.
- AC-d82b411a (US-012): Given a request is resolved within its SLA window, When the request is viewed after resolution, Then it is marked as resolved within SLA and no breach flag is shown.
- AC-46f8109b (US-013): Given I am logged in as a Supervisor, When I navigate to the downtime report, Then I see a table showing each machine, the number of resolved requests in the selected period, and the total downtime hours (calculated as sum of time from request creation to resolution for resolved requests).
- AC-92f7fbf0 (US-013): Given I select a specific month, When the report is rendered, Then only requests created and resolved within that month are included in the calculation.
- AC-826f7923 (US-013): Given no requests exist for a machine in the selected period, When I view the report, Then that machine shows zero requests and zero downtime hours.
- AC-d01ba8e1 (US-013): Given the current month's data is viewed, When the report is rendered, Then it includes all resolved requests up to the current moment and notes that the period is incomplete.
- AC-d99a7b4c (US-014): Given I navigate to the FixIt login page, When I click 'Sign in', Then I am redirected to the organisation's existing identity provider (IdP) login page.
- AC-ec74634a (US-014): Given I authenticate successfully with my organisational credentials, When the IdP redirects me back to FixIt, Then I am logged in with the role assigned to my account in the directory.
- AC-519e32c1 (US-014): Given my organisational account is disabled or my session expires, When I attempt to access any FixIt page, Then I am redirected to the login page and cannot access any data.
- AC-9e3efa04 (US-014): Given I have not been assigned a FixIt role in the directory, When I authenticate successfully, Then I see an access-denied message explaining that my account has not been provisioned.
- AC-93a373bc (US-018): Given a request is submitted at any time (including outside defined working hours), When the request is created, Then it is immediately visible in the Technician queue and on the Supervisor dashboard with its creation timestamp.
- AC-7f50f8fd (US-018): Given working hours are defined in system configuration, When a request is created outside those hours, Then the request is labelled 'Raised out of hours' and the SLA clock behaviour (continuous or shift-hours-only) is applied as configured.
- AC-5c24b9fa (US-018): Given the SLA clock is configured to run on shift hours only, When an out-of-hours request is viewed at shift start, Then the SLA countdown reflects only the elapsed staffed time, not wall-clock time.
- AC-8db80cb2 (US-018): Given the SLA clock is configured to run continuously, When an out-of-hours request is viewed at shift start, Then the SLA countdown reflects total elapsed wall-clock time since creation.
- AC-fa39f6dd (US-019): Given the API is deployed, When I make an authenticated GET request to the requests endpoint with a date-range filter, Then I receive a JSON response conforming to the published OpenAPI 3.x schema containing request records with all status transition timestamps.
- AC-554d9609 (US-019): Given the API is deployed, When I make an authenticated GET request to the downtime summary endpoint, Then I receive aggregated downtime hours per machine per period in the documented schema.
- AC-a7e9fc8a (US-019): Given I make a request with an invalid or expired authentication token, When the API processes the request, Then it returns HTTP 401 and does not expose any data.
- AC-e3971f82 (US-019): Given the API schema is versioned, When a breaking change is introduced, Then the previous version remains available for a documented deprecation period of at least 90 days.
- AC-a9dc8a19 (US-020): Given I am logged in as a Technician, When I open a request and navigate to the machine's history, Then I see a list of all resolved requests for that machine in reverse chronological order, each showing the fault description, resolution note, priority, and resolved timestamp.
- AC-c927ac3b (US-020): Given a machine has no prior resolved requests, When I view its history, Then I see an empty state message confirming no prior faults are recorded.
- AC-4331b84e (US-020): Given I am logged in as any role, When I view a machine's request history, Then I can read resolution notes but cannot edit or delete them.
- AC-d1a5bd4b (US-020): Given a machine has been deactivated, When I view its history, Then the full resolved request history is still accessible and the machine is labelled as deactivated.

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

- **nfr: nfr priority order** → NFR priority order set as: performance → availability → reliability → security → data_management → usability → accessibility → scalability → observability → integration → maintainability → privacy (`nfr-priority-order`)
- **nfr: adrs and well architected review** → IBM-METH-001 mandates ADR for every significant architecture decision and a Well-Architected review before production go-live; both are adopted as mandatory governance gates for FixIt (`adrs-and-well-architected-review`)
- **other: shift left quality gates** → Adopt shift-left testing with automated quality gates at PR, merge, INT, and PreProd stages (`shift-left-quality-gates`)
- **other: testcontainers integration testing** → Use Testcontainers for Spring Boot integration tests against an ephemeral Aurora-compatible PostgreSQL container (`testcontainers-integration-testing`)
- **other: terraform helm iac gitops** → All AWS infrastructure provisioned via Terraform; all EKS workloads deployed via Helm charts managed in a GitOps repository. No console click-ops in any environment. (`terraform-helm-iac-gitops`)
- **other: eks pod sizing hpa autoscaling** → Spring Boot API pods sized at 500m CPU / 512 Mi memory (request) and 1000m CPU / 1 Gi memory (limit); HPA min 2 / max 6 replicas in production on 60% CPU threshold. (`eks-pod-sizing-hpa-autoscaling`)
- **other: cicd github actions ecr helm** → CI/CD pipeline implemented in GitHub Actions: build → unit test → SAST (SonarQube) → dependency scan (Snyk) → Docker build → ECR push → Helm deploy to Dev → integration test → Helm deploy to Pre-Prod (approval gate) → performance test → Helm deploy to Prod (manual approval gate). (`cicd-github-actions-ecr-helm`)
- **other: opentelemetry prometheus grafana observability** → OpenTelemetry instrumentation on Spring Boot API (Micrometer + OTLP exporter); Prometheus for metrics collection; Grafana for dashboards; CloudWatch Alarms for alerting. SLO definitions with error budgets per IBM-OBS-001. (`opentelemetry-prometheus-grafana-observability`)
- **security: tls mandatory all hops** → TLS 1.2+ mandatory on all inbound traffic at AWS API Gateway; TLS 1.3 preferred; TLS enforced on Aurora connections; SMTP STARTTLS for Phase 2 notifications. (`tls-mandatory-all-hops`)
- **What pagination style should list endpoints use?** → Cursor-based pagination using an opaque base64-encoded cursor encoding the last-seen sort key (e.g. created_at + request_id). All list endpoints accept ?cursor=&limit= and return a CursorPage object. (`cursor-pagination-opaque`)
- **What format should primary keys use?** → UUID v4 generated server-side via uuid_generate_v4() for all primary keys. A separate human-readable reference_number (e.g. REQ-20250601-0042) is generated for display. (`request-id-format-uuid`)
- **How should the human-readable request reference number be generated?** → Format: REQ-YYYYMMDD-NNNN where NNNN is a zero-padded daily sequence. Generated server-side at request creation. Stored in maintenance_request.reference_number with a UNIQUE constraint. (`reference-number-format`)
- **What are the permitted status transitions and who can perform them?** → OPEN→OPEN (assign, TECHNICIAN only, unassigned requests only); OPEN→IN_PROGRESS (start work, TECHNICIAN who is the assignee only); IN_PROGRESS→RESOLVED (resolve with mandatory non-empty resolutionNote, TECHNICIAN who is the assignee only). No other transitions permitted. All transitions are immutable once written. (`state-machine-transitions`)
- **What is the minimum length for a resolution note?** → Minimum 10 characters, enforced server-side. Empty or whitespace-only notes are rejected with HTTP 400. (`resolution-note-min-length`)
- **How are SLA deadlines calculated and stored?** → At request creation, the API reads SLA thresholds from system_config (sla.critical.threshold_minutes, sla.medium.threshold_minutes, sla.low.threshold_minutes) and the sla.clock_mode. For CONTINUOUS mode, sla_deadline_at = created_at + threshold. For SHIFT_HOURS_ONLY mode, sla_deadline_at is calculated by advancing created_at by threshold minutes counting only shift hours (shift.start_time to shift.end_time in shift.timezone). The deadline is stored in both maintenance_request.sla_deadline_at and sla_record. SLA status (ON_TRACK/AT_RISK/BREACHED/MET) is computed on read, not stored, using sla_deadline_at and sla.at_risk.lead_minutes. (`sla-calculation-engine`)
- **How is the out-of-hours flag set on a request?** → At request creation, the API compares created_at (in shift.timezone) against shift.start_time and shift.end_time from system_config. If outside shift hours, out_of_hours_flag is set to true. This flag is immutable after creation. (`out-of-hours-flag`)
- **How are real-time updates delivered to clients?** → Server-Sent Events (SSE) via Spring WebFlux SseEmitter on GET /api/v1/events/stream. The API maintains a per-connection emitter registry. On any state-changing operation (status transition, comment, machine update), the service broadcasts a typed SSE event to all active emitters. JWT is validated on SSE connection establishment; role-scoped filtering is applied (Operators receive only their own request events). (`sse-implementation`)
- **How is role-based access control enforced?** → Spring Security reads the 'roles' claim from the Keycloak-issued JWT. Three roles: OPERATOR, TECHNICIAN, SUPERVISOR. Endpoint-level @PreAuthorize annotations enforce role requirements. Row-level scoping (Operator sees only own requests) is enforced in the service layer by comparing JWT sub claim to created_by_user_id. HTTP 403 is returned for role violations; HTTP 401 for missing/invalid tokens. (`rbac-three-roles`)
- **How are deactivated machines handled?** → Machines are soft-deleted via is_active=false. The GET /api/v1/machines endpoint defaults to activeOnly=true, hiding deactivated machines from the Operator selector. Deactivated machines remain in the database and are still referenced by historical requests. The machine name is stored denormalised on maintenance_request so name changes propagate immediately to all views via the FK join. (`machine-soft-delete`)
- **How do configuration changes take effect within 5 minutes without a restart?** → system_config is read from the database on each SLA calculation and request creation (not cached at startup). A short-lived in-process cache (TTL 60 seconds) is used to avoid per-request DB reads. This guarantees changes take effect within 60 seconds, well within the 5-minute NFR. (`config-hot-reload`)
- **How is downtime calculated for the report?** → Downtime per request = resolved_at - created_at (wall-clock minutes). Total downtime per machine per period = SUM of downtime for all RESOLVED requests where created_at AND resolved_at fall within the selected month. Requests created in the period but not yet resolved are excluded. The current month is flagged as periodIncomplete=true. (`downtime-calculation-definition`)
- **How are immutable records (status history, comments, resolution notes) protected?** → No UPDATE or DELETE SQL is ever issued against request_status_history or request_comment tables. The Spring Data repositories for these entities expose only save() (insert) and find*() methods. Resolution notes on maintenance_request are set once on RESOLVED transition and the service layer rejects any subsequent PATCH that attempts to modify resolution_note or resolved_at. Database-level: REVOKE UPDATE, DELETE ON request_status_history, request_comment FROM app_user. (`immutability-enforcement`)
- **How is the 7-year retention requirement implemented?** → No automated deletion is implemented in Phase 1. All request records, status history, and comments are retained indefinitely in Aurora. A future Phase 2 archival job will move records older than 7 years from resolved_at to S3 Glacier before deletion. The export API (US-019) enables data extraction before any future archival. (`data-retention-7-years`)
- **Is the 24-hour overdue threshold hardcoded or configurable?** → Configurable via system_config key 'overdue.threshold_minutes' (default 1440 = 24 hours). The dashboard query uses this value from the config cache. (`overdue-threshold-configurable`)
- **How is API versioning handled for the export endpoints?** → URL path versioning: /api/v1/... All endpoints are v1. When a breaking change is required, /api/v2/... is introduced and v1 is maintained for a minimum 90-day deprecation period with a Deprecation response header and a sunset date. (`export-api-versioning`)
- **What PII is stored and how is data minimisation applied?** → PII stored: user_id (UUID, system-generated), display_name (from JWT claim). No email, phone, or other personal data is stored. PII fields are annotated in the DDL comments. Data minimisation is reviewed at each release per NFR-15. (`pii-data-minimisation`)
- **How is priority ordering implemented in the database query?** → ORDER BY CASE priority WHEN 'CRITICAL' THEN 1 WHEN 'MEDIUM' THEN 2 WHEN 'LOW' THEN 3 END ASC, created_at ASC. The composite index idx_mr_status_priority_created supports this query efficiently. (`priority-sort-order`)

## Contract

The endpoints, schemas, tables and events above are defined in `contracts/` in this repository. Implement them as written; a change to the contract is a change to the design.
