# aws-alb

LLD component `cmp:aws-alb` · AWS ALB, AWS Certificate Manager

AWS Application Load Balancer — TLS 1.2+ termination, HTTP→HTTPS redirect, public-facing ingress, ACM certificate management

## Stories

- `US-014`

## Acceptance criteria

- AC-d99a7b4c (US-014): Given I navigate to the FixIt login page, When I click 'Sign in', Then I am redirected to the organisation's existing identity provider (IdP) login page.
- AC-ec74634a (US-014): Given I authenticate successfully with my organisational credentials, When the IdP redirects me back to FixIt, Then I am logged in with the role assigned to my account in the directory.
- AC-519e32c1 (US-014): Given my organisational account is disabled or my session expires, When I attempt to access any FixIt page, Then I am redirected to the login page and cannot access any data.
- AC-9e3efa04 (US-014): Given I have not been assigned a FixIt role in the directory, When I authenticate successfully, Then I see an access-denied message explaining that my account has not been provisioned.

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

## Contract

The endpoints, schemas, tables and events above are defined in `contracts/` in this repository. Implement them as written; a change to the contract is a change to the design.
