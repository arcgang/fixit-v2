# fixit-api-service

LLD component `cmp:fixit-api-service` · Java 21, Spring Boot 3, Spring Security, EKS Pod · seeded from Java (Spring Boot)

Spring Boot REST API — core business logic, request lifecycle state machine, server-side RBAC, SLA calculation, SSE event emission, audit logging, machine register, configuration management

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
- `US-018`
- `US-019`
- `US-020`

## Endpoints

- `GET /events`
- `GET /machines`
- `POST /machines`
- `GET /requests`
- `POST /requests`
- `GET /dashboard`
- `GET /config/sla`
- `PUT /config/sla`
- `GET /requests/queue`
- `GET /export/downtime`
- `GET /export/requests`
- `GET /machines/{machineId}`
- `PUT /machines/{machineId}`
- `GET /requests/{requestId}`
- `PUT /requests/{requestId}/start`
- `PUT /requests/{requestId}/assign`
- `GET /machines/{machineId}/history`
- `PUT /requests/{requestId}/resolve`
- `GET /requests/{requestId}/comments`
- `POST /requests/{requestId}/comments`
- `PUT /machines/{machineId}/deactivate`
- `POST /v1/requests`
- `GET /v1/requests`
- `PUT /v1/requests/{id}/assign`
- `PUT /v1/requests/{id}/start`
- `PUT /v1/requests/{id}/resolve`
- `GET /POST`
- `GET /v1/dashboard`
- `PUT /v1/machines`
- `PUT /v1/config/sla`
- `GET /v1/export/requests`
- `GET /v1/machines/{id}/history`

## Entities

- `ent:audit-events`
- `ent:machines`
- `ent:maintenance-requests`
- `ent:request-comments`
- `ent:sla-config`
- `ent:users`
- `ent:maintenancerequest`
- `ent:machine`
- `ent:user`
- `ent:requestcomment`
- `ent:auditevent`
- `ent:slaconfig`

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

- **other: ibm meth 001 adr well architected** → Apply IBM-METH-001: author ADRs for all significant architecture decisions; conduct IBM Well-Architected review before production go-live (`ibm-meth-001-adr-well-architected`)
- **other: test framework backend** → JUnit 5 + Mockito for backend unit tests; Spring Boot Test + Testcontainers for integration tests (`test-framework-backend`)
- **other: test framework frontend** → Jest + React Testing Library for frontend unit tests; Playwright for E2E and accessibility tests (`test-framework-frontend`)
- **security: sast tooling** → SonarQube for code quality gate + Checkmarx for security-specific SAST, both running on every PR (`sast-tooling`)
- **security: dast tooling** → OWASP ZAP baseline scan on merge to main; OWASP ZAP active scan on deploy to pre-production (`dast-tooling`)
- **security: penetration test requirement** → Full penetration test by approved external vendor required before go-live, targeting RBAC, IDOR, and privilege escalation (`penetration-test-requirement`)
- **other: coverage threshold 80 percent** → ≥ 80% line coverage enforced as a hard pipeline gate for both backend (JaCoCo) and frontend (Istanbul) (`coverage-threshold-80-percent`)
- **other: flaky test quarantine policy** → Flaky tests quarantined within 24 hours; must be fixed or deleted within one sprint; quarantined tests do not block pipeline but are tracked (`flaky-test-quarantine-policy`)
- **other: adr standard ibm meth 001** → All significant architecture decisions documented as Architecture Decision Records (ADRs) per IBM-METH-001 and the FixCorp ADR Standard; no 'temporary' solutions exempt (`adr-standard-ibm-meth-001`)
- **auth: identity keycloak arb exception** → Use Keycloak as the OIDC identity provider; ARB exception required against org standard Okta (dec-004) (`identity-keycloak-arb-exception`)
- **security: server side rbac three roles** → Server-side RBAC enforced at the Spring Boot API layer for three roles: Operator, Technician, Supervisor (`server-side-rbac-three-roles`)
- **nfr: tls every hop** → TLS 1.2+ enforced on every network hop — ALB ingress, ALB→EKS, and intra-cluster service calls (`tls-every-hop`)
- **security: secrets aws secrets manager** → All credentials stored in AWS Secrets Manager with automatic rotation; zero hardcoded secrets (`secrets-aws-secrets-manager`)
- **auth: irsa for all service to service auth** → IAM Roles for Service Accounts (IRSA) for all EKS pod-to-AWS-service authentication; zero static credentials (`irsa-for-all-service-to-service-auth`)
- **security: vpc endpoints no internet egress for aws services** → VPC Interface Endpoints (PrivateLink) for all AWS service calls — no internet egress for Secrets Manager, CloudWatch Logs, KMS, ECR, STS (`vpc-endpoints-no-internet-egress-for-aws-services`)
- **other: github actions cicd pipeline** → GitHub Actions as CI/CD pipeline with Terraform Cloud for infrastructure; manual approval gate for production deployments; feature flags via AWS AppConfig (`github-actions-cicd-pipeline`)
- **auth: mfa keycloak realm policy** → MFA enforced for all interactive sessions via Keycloak realm Required Action policy (`mfa-keycloak-realm-policy`)
- **security: server side rbac spring security** → Server-side RBAC enforced exclusively at Spring Boot API layer via @PreAuthorize — client-side UI hiding is supplementary only (`server-side-rbac-spring-security`)
- **security: tls every hop including intracluster** → TLS 1.2+ enforced on every network hop including intra-cluster JDBC and service-to-service calls (`tls-every-hop-including-intracluster`)
- **What pagination style should all list endpoints use?** → Cursor-based pagination using an opaque base64-encoded token encoding (last_id, last_sort_value). All list endpoints accept ?cursor=&limit= and return a CursorPage object. (`cursor-pagination-opaque-token`)
- **What are the valid request status transitions and who may perform them?** → Open→Open (assign, Technician only); Open→In Progress (start, assignee Technician only); In Progress→Resolved (resolve with mandatory note, assignee Technician only). No backward transitions. All transitions are immutable once written. (`state-machine-transitions`)
- **When is the SLA target timestamp calculated and stored?** → sla_target_at is calculated and persisted at request creation time by reading the current sla_config for the chosen priority. If clock_mode=shift_hours_only and the request is out-of-hours, sla_target_at = next shift start + sla_hours. If clock_mode=continuous, sla_target_at = created_at + sla_hours. (`sla-calculation-at-creation`)
- **How is 'at risk' SLA status computed?** → A request is 'at_risk' when now() >= sla_target_at - 15 minutes AND status != Resolved. It is 'breached' when now() >= sla_target_at AND status != Resolved. The slaStatus field is computed at query time and returned in every MaintenanceRequest response. (`sla-at-risk-threshold`)
- **How is out-of-hours detection implemented?** → At request creation, the API reads shift_start_time and shift_end_time from sla_config (per priority or a global config row). If the current UTC time (converted to the configured timezone) falls outside the shift window, out_of_hours=true is set on the request. (`out-of-hours-detection`)
- **How is downtime calculated for the downtime report?** → Downtime for a resolved request = resolved_at - created_at in minutes, stored as downtime_minutes at resolution time. The export/downtime endpoint sums downtime_minutes per machine for requests where created_at AND resolved_at both fall within the requested period. (`downtime-calculation-definition`)
- **How is immutability of audit records enforced?** → The audit_events table is INSERT-only. The application database role is granted INSERT only (no UPDATE, DELETE) on audit_events. No API endpoint exposes UPDATE or DELETE for audit records. This is enforced at the PostgreSQL grant level. (`audit-log-insert-only`)
- **How is server-side RBAC enforced?** → Spring Security @PreAuthorize annotations on every controller method. Role claims are extracted from the Keycloak-issued JWT (claim: realm_roles or resource_access). Roles: OPERATOR, TECHNICIAN, SUPERVISOR. Unauthorised access returns HTTP 403. (`rbac-three-roles-spring-security`)
- **How are deactivated machines handled to preserve historical data?** → Deactivation sets active=false, deactivated_at, and deactivated_by_user_id on the machines row. The machine row is never deleted. Historical maintenance_requests retain their machine_id FK. GET /v1/machines?activeOnly=true (default) excludes inactive machines from the Operator selector. (`machine-soft-delete`)
- **How are user records created and kept in sync with Keycloak?** → On every authenticated API call, the service upserts the users table using the JWT sub claim as user_id, updating display_name, role, and last_login_at. No separate user provisioning endpoint is needed. (`user-upsert-on-login`)
- **Can comments be edited or deleted?** → No. request_comments is append-only. No UPDATE or DELETE API endpoints are exposed for comments. The database role is granted INSERT and SELECT only on request_comments. (`comments-append-only`)
- **How is SLA configuration stored — one row per priority or versioned history?** → One active row per priority level (UNIQUE constraint on priority_level). PUT /v1/config/sla performs an UPSERT. Previous values are not retained in sla_config; changes are logged in audit_events. (`sla-config-upsert-by-priority`)
- **What format are request IDs?** → UUID v4 generated by PostgreSQL uuid_generate_v4(). Exposed as the requestId field in all API responses. (`request-id-format-uuid`)
- **Where is all FixIt data stored?** → All data is stored exclusively in AWS eu-west-1 (Ireland). Aurora PostgreSQL, CloudWatch Logs, S3 archival, and Secrets Manager are all eu-west-1. No cross-border transfer outside the EU. (`data-residency-eu-west-1`)
- **How is the 7-year retention requirement for request records enforced?** → Aurora PostgreSQL retains all records. CloudWatch Logs exports to S3 (Glacier after 90 days) via lifecycle rules. No DELETE is permitted on maintenance_requests, request_comments, or audit_events by the application role. Retention enforced at the storage layer. (`seven-year-retention`)
- **How does the API handle deactivation of a machine with open requests?** → PUT /v1/machines/{machineId}/deactivate checks for open/in-progress requests. If any exist and confirm=false (or not set), it returns HTTP 409 with the open request list in the Error.details array. The client must re-submit with confirm=true to proceed. (`deactivate-with-open-requests-warning`)
- **How is the priority sort order implemented in the technician queue?** → The query uses CASE WHEN priority='Critical' THEN 1 WHEN priority='Medium' THEN 2 ELSE 3 END as priority_rank, then created_at ASC. A partial index on (priority, created_at) WHERE status='Open' supports this query efficiently. (`priority-sort-order-in-queue`)
- **How are SLA thresholds (Critical <1 h, Medium ≤4 h, Low ≤8 h) and shift-hours configuration stored and updated so that changes take effect within 5 minutes without an application restart?** → SLA thresholds and shift-hours definitions are stored as rows in a dedicated configuration table in Amazon Aurora PostgreSQL (cmp:amazon-aurora-postgresql), managed via a Supervisor-accessible admin endpoint on cmp:fixit-api-service (RBAC-protected, Supervisor role only). The Spring Boot service (cmp:fixit-api-service) caches these values in-process with a TTL of 60 seconds using Spring's @Scheduled cache-eviction; on each cache miss the values are re-read from Aurora. This guarantees any saved change is picked up within at most 60 seconds — well within the 5-minute SLA — without a pod restart. All configuration changes are written to the audit log (cmp:aws-cloudwatch-logs) with actor, timestamp, old value, and new value. (`sla-config-hot-reload`)

## Contract

The endpoints, schemas, tables and events above are defined in `contracts/` in this repository. Implement them as written; a change to the contract is a change to the design.
