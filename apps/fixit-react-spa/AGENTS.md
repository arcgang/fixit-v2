# fixit-react-spa

LLD component `cmp:fixit-react-spa` · React 18, TypeScript, Vite, AWS CloudFront, Amazon S3 · seeded from React

Browser-based SPA serving all three personas: Operator request form and My Requests, Technician priority queue and request detail, Supervisor dashboard and machine register management. Handles OIDC PKCE flow and SSE connection lifecycle.

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

- **security: p2 security testing schedule** → Classify FixIt as P2 security priority requiring full pen test by approved vendor before launch and annually, plus automated SAST/DAST in CI (`p2-security-testing-schedule`)
- **auth: keycloak oidc arb exception** → Use Keycloak as the OIDC identity provider for FixIt, with an ARB exception declared against the org-wide Okta mandate (dec-004). (`keycloak-oidc-arb-exception`)
- **security: server side rbac tls mandatory** → Server-side RBAC enforcement is mandatory for all role-restricted endpoints; TLS 1.2+ is required on every hop; client-side UI hiding is explicitly insufficient. (`server-side-rbac-tls-mandatory`)
- **security: aws secrets manager irsa** → AWS Secrets Manager with IRSA (IAM Roles for Service Accounts) for all application credentials; no hardcoded secrets in code, images, or Helm values. (`aws-secrets-manager-irsa`)
- **nfr: three tier spa not microservices** → Three-tier SPA architecture (React → Spring Boot monolith → Aurora) rather than microservices decomposition, appropriate for bounded scale and team size. (`three-tier-spa-not-microservices`)
- **other: vpc subnet topology three tier** → Three-tier VPC subnet topology per AZ: public (NAT Gateway, ALB), private-app (EKS node groups), private-data (Aurora, VPC endpoints). All EKS workloads in private-app subnets. (`vpc-subnet-topology-three-tier`)
- **auth: oidc pkce mfa all users** → OIDC Authorization Code + PKCE with mandatory MFA for all three user types (Operator, Technician, Supervisor) via Keycloak realm policy. (`oidc-pkce-mfa-all-users`)
- **auth: irsa workload identity no static keys** → IRSA (IAM Roles for Service Accounts) used for all EKS pod-to-AWS-service authentication; no static IAM access keys anywhere in the solution. (`irsa-workload-identity-no-static-keys`)
- **security: server side rbac three roles** → Three-role RBAC model (OPERATOR, TECHNICIAN, SUPERVISOR) enforced server-side by Spring Boot Security on every API request; client-side UI hiding explicitly insufficient. (`server-side-rbac-three-roles`)
- **security: aws secrets manager all credentials** → AWS Secrets Manager is the sole credential store for all application secrets; automatic rotation ≤90 days; no credentials in code, images, Helm values, or environment variables. (`aws-secrets-manager-all-credentials`)
- **security: waf owasp cloudfront** → AWS WAF with OWASP Top 10 managed rule group attached to CloudFront distribution; rate-based rules to mitigate brute-force on Keycloak login redirect. (`waf-owasp-cloudfront`)
- **Where should JWT access tokens and refresh tokens be stored in the browser?** → Tokens are stored exclusively in JavaScript memory (React context / in-memory state). No localStorage, sessionStorage, or cookies are used for token storage. (`token-storage-memory-only`)
- **How should the SPA receive real-time status updates within the 60-second NFR without a page reload?** → The SPA opens a single Server-Sent Events (SSE) connection to GET /events/stream per active session. The connection is managed in a React context provider and automatically reconnects on drop. No WebSocket or polling is used. (`sse-for-real-time-updates`)
- **Should SLA status badges (At risk, SLA breached) be computed client-side or server-side?** → SLA status is computed client-side in the SPA using the slaTargetMinutes field returned by the API alongside each request. A 60-second interval timer re-evaluates badges without re-fetching. The server also returns a slaStatus field for initial render and SSE-pushed updates. (`client-side-sla-computation`)
- **How does the SPA enforce that each persona only sees their permitted views?** → On login the SPA decodes the role claim from the JWT and stores it in React context. Route guards redirect unauthorised role attempts to a 403 error page. All action buttons (Assign to me, Start work, Resolve, machine management) are conditionally rendered based on role. Server-side RBAC is the authoritative enforcement; client-side gating is UX-only. (`role-based-routing-and-ui-gating`)
- **Where is form validation performed for request submission and machine management forms?** → Required-field validation (machine selector, description, priority, machine name, resolution note) is performed client-side before any API call. The SPA highlights each missing field with an inline error message. Server-side validation errors (4xx) are also surfaced as form-level error banners. (`form-validation-client-side-first`)
- **How does the SPA ensure only active machines appear in the request submission machine selector?** → The SPA calls GET /machines with an implicit or explicit status=active query parameter when loading the new request form. Deactivated machines are never fetched for this selector. The full machine list (including inactive) is only fetched on the Supervisor machine management page. (`machine-selector-active-only-filter`)
- **How does the SPA handle the deactivation warning for machines with open/in-progress requests?** → Before showing the deactivation confirmation dialog, the SPA fetches GET /requests?machineId={id}&status=Open,In+Progress. If any results are returned, a warning dialog lists those requests and requires an explicit second confirmation. If no open requests exist, a simple confirmation dialog is shown. (`deactivation-confirmation-flow`)
- **How does the SPA obtain SLA thresholds and shift-hours configuration for display and SLA computation?** → On application boot (after successful authentication) the SPA fetches GET /config once and caches the result in React context. The config is re-fetched every 5 minutes via a background interval to pick up admin changes without a restart. SLA target minutes per priority are derived from this config. (`sla-config-boot-fetch`)
- **What pagination style is used for list views (My Requests, queue, machine list, comment history, machine history)?** → All list endpoints use cursor-based pagination per the conventions (cursor + limit query params, CursorPage response). The SPA renders a 'Load more' button that passes next_cursor on subsequent calls. Default limit is 25 for request lists and 50 for machine lists. (`cursor-pagination-for-lists`)
- **What happens when the SSE connection to /events/stream drops?** → The SPA implements exponential back-off reconnection (1 s, 2 s, 4 s, max 30 s) using the browser's native EventSource API. On reconnect, the SPA re-fetches the relevant list endpoint to catch any missed events during the disconnection window. A subtle 'Reconnecting…' indicator is shown to the user. (`sse-reconnect-strategy`)
- **What does the SPA show if the Keycloak IdP is unreachable during login or token refresh?** → If the token endpoint does not respond within 10 seconds, the SPA displays a full-page 'Service unavailable – identity provider unreachable. Please try again shortly.' message. No partial session is created and no data is accessible. (`idp-timeout-user-message`)
- **What user personal data does the SPA store or display?** → The SPA displays only display name, role, and system-generated user ID (from JWT claims) for request attribution, comment authorship, and assignee display. No email addresses, phone numbers, or other PII are requested or stored client-side beyond the JWT lifetime. (`no-personal-data-beyond-minimum`)
- **How does the SPA meet WCAG 2.1 Level AA and the shop-floor tablet usability requirement?** → All interactive components use semantic HTML elements with ARIA labels. Colour contrast ratios meet AA thresholds. Touch targets are ≥44×44 px. The layout is responsive (mobile-first, min 320 px). Forms are designed for single-hand tablet use with large tap targets and minimal required fields. Automated axe-core scans run in CI. (`wcag-aa-and-tablet-optimisation`)
- **How is the React SPA deployed and served?** → The SPA is built with Vite into static assets (HTML, JS, CSS) and deployed to an Amazon S3 bucket. AWS CloudFront serves the assets with HTTPS-only (TLS 1.2+), with AWS WAF (OWASP Top 10 managed rule group) attached to the distribution. The S3 bucket is private; CloudFront uses an Origin Access Control (OAC) policy. (`cloudfront-s3-static-hosting`)
- **How does the SPA display the 'Raised out of hours' label and SLA clock mode?** → The API returns an outOfHours boolean and slaClockMode ('continuous' | 'shift-hours-only') on each request. The SPA renders a 'Raised out of hours' badge when outOfHours is true. SLA elapsed time computation uses slaElapsedMinutes (server-computed staffed elapsed time) when slaClockMode is 'shift-hours-only', and wall-clock elapsed time when 'continuous'. (`out-of-hours-label-display`)

## Contract

The endpoints, schemas, tables and events above are defined in `contracts/` in this repository. Implement them as written; a change to the contract is a change to the design.
