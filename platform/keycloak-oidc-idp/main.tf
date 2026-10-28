# keycloak-oidc-idp — Keycloak 24+, OIDC, SAML 2.0, EKS Pod (HA)
# LLD component cmp:keycloak-oidc-idp. Provision and configure it here; nothing in this module is applied
# until a pull request adds the provider resources and the plan is reviewed.

terraform {
  required_providers {
    keycloak = {
      source  = "mrparkers/keycloak"
      version = "~> 4.4"
    }
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

variable "environment" {
  type = string
}

variable "spa_cloudfront_url" {
  type        = string
  description = "HTTPS CloudFront distribution URL for the FixIt SPA"
}

variable "refresh_token_ttl_seconds" {
  type        = number
  default     = 28800
  description = "Idle and max session lifespan in seconds for the SPA OIDC client (default 8 h)"
}

# ---------------------------------------------------------------------------
# Keycloak admin credentials sourced from AWS Secrets Manager
# ---------------------------------------------------------------------------

data "aws_secretsmanager_secret" "keycloak_admin" {
  name = "fixit/${var.environment}/keycloak-admin-credentials"
}

data "aws_secretsmanager_secret_version" "keycloak_admin" {
  secret_id = data.aws_secretsmanager_secret.keycloak_admin.id
}

locals {
  keycloak_admin = jsondecode(data.aws_secretsmanager_secret_version.keycloak_admin.secret_string)
}

# ---------------------------------------------------------------------------
# Provider
# ---------------------------------------------------------------------------

provider "keycloak" {
  client_id = "admin-cli"
  username  = local.keycloak_admin["username"]
  password  = local.keycloak_admin["password"]
  url       = "https://auth.fixit.fixcorp.internal"
}

# ---------------------------------------------------------------------------
# Realm
# ---------------------------------------------------------------------------

resource "keycloak_realm" "fixit" {
  realm                    = "fixit"
  display_name             = "FixIt"
  enabled                  = true
  ssl_required             = "external"
  internationalization_enabled = false
}

# ---------------------------------------------------------------------------
# Realm roles
# ---------------------------------------------------------------------------

resource "keycloak_role" "fixit_operator" {
  realm_id = keycloak_realm.fixit.id
  name     = "fixit-operator"
}

resource "keycloak_role" "fixit_technician" {
  realm_id = keycloak_realm.fixit.id
  name     = "fixit-technician"
}

resource "keycloak_role" "fixit_supervisor" {
  realm_id = keycloak_realm.fixit.id
  name     = "fixit-supervisor"
}

# ---------------------------------------------------------------------------
# OIDC public client (PKCE S256)
# ---------------------------------------------------------------------------

resource "keycloak_openid_client" "fixit_spa" {
  realm_id    = keycloak_realm.fixit.id
  client_id   = "fixit-spa"
  access_type = "PUBLIC"

  standard_flow_enabled  = true
  implicit_flow_enabled  = false

  pkce_code_challenge_method = "S256"

  # NFR: access token TTL <= 3600 s
  access_token_lifespan = "3600"

  # Session timeouts driven by refresh_token_ttl_seconds variable
  client_session_idle_timeout = tostring(var.refresh_token_ttl_seconds)
  client_session_max_lifespan = tostring(var.refresh_token_ttl_seconds)

  valid_redirect_uris = [var.spa_cloudfront_url]
  web_origins         = [var.spa_cloudfront_url]
}

# ---------------------------------------------------------------------------
# Custom browser authentication flow — MFA enforcement + role-presence check
# ---------------------------------------------------------------------------
#
# Flow structure:
#   fixit-browser (top-level)
#     cookie                         ALTERNATIVE  — skip re-auth for active sessions
#     identity-provider-redirector   ALTERNATIVE  — org IdP redirect
#     username-password-form         REQUIRED     — credential step
#     fixit-role-check (CONDITIONAL) CONDITIONAL  — deny users with no FixIt role
#       role-condition-operator      REQUIRED     — negate: user does NOT have fixit-operator
#       role-condition-technician    REQUIRED     — negate: user does NOT have fixit-technician
#       role-condition-supervisor    REQUIRED     — negate: user does NOT have fixit-supervisor
#       deny-no-role                 REQUIRED     — deny access when all three conditions hold
#     fixit-mfa (sub-flow)           REQUIRED     — at least one second factor required
#       totp                         ALTERNATIVE  — TOTP (authenticator app)
#       webauthn                     ALTERNATIVE  — WebAuthn (hardware key / platform)
#
# Role-check logic: each conditional-user-role execution uses negate=true and
# requirement=REQUIRED. Keycloak evaluates all REQUIRED conditions in the CONDITIONAL
# sub-flow; the sub-flow's body (deny-no-role) fires only when every condition
# returns TRUE (AND semantics). A condition returns TRUE when the user does NOT have
# the named role (negate=true). So deny fires only when the user lacks all three roles.

resource "keycloak_authentication_flow" "fixit_browser" {
  realm_id    = keycloak_realm.fixit.id
  alias       = "fixit-browser"
  description = "FixIt browser flow: cookie → IdP redirect → credentials → role check → MFA"
}

# Standard browser entry steps

resource "keycloak_authentication_execution" "cookie" {
  realm_id          = keycloak_realm.fixit.id
  parent_flow_alias = keycloak_authentication_flow.fixit_browser.alias
  authenticator     = "auth-cookie"
  requirement       = "ALTERNATIVE"
}

resource "keycloak_authentication_execution" "identity_provider_redirector" {
  realm_id          = keycloak_realm.fixit.id
  parent_flow_alias = keycloak_authentication_flow.fixit_browser.alias
  authenticator     = "identity-provider-redirector"
  requirement       = "ALTERNATIVE"

  depends_on = [keycloak_authentication_execution.cookie]
}

resource "keycloak_authentication_execution" "username_password" {
  realm_id          = keycloak_realm.fixit.id
  parent_flow_alias = keycloak_authentication_flow.fixit_browser.alias
  authenticator     = "auth-username-password-form"
  requirement       = "REQUIRED"

  depends_on = [keycloak_authentication_execution.identity_provider_redirector]
}

# Role-presence check: CONDITIONAL sub-flow
# Fires the Deny Access step when the user holds none of the three FixIt roles.

resource "keycloak_authentication_subflow" "role_check" {
  realm_id          = keycloak_realm.fixit.id
  parent_flow_alias = keycloak_authentication_flow.fixit_browser.alias
  alias             = "fixit-role-check"
  provider_id       = "conditional"
  requirement       = "CONDITIONAL"

  depends_on = [keycloak_authentication_execution.username_password]
}

resource "keycloak_authentication_execution" "role_condition_operator" {
  realm_id          = keycloak_realm.fixit.id
  parent_flow_alias = keycloak_authentication_subflow.role_check.alias
  authenticator     = "conditional-user-role"
  requirement       = "REQUIRED"
}

resource "keycloak_authentication_execution_config" "role_condition_operator" {
  realm_id     = keycloak_realm.fixit.id
  execution_id = keycloak_authentication_execution.role_condition_operator.id
  alias        = "fixit-role-condition-operator"
  config = {
    condUserRole = "fixit-operator"
    negate       = "true"
  }
}

resource "keycloak_authentication_execution" "role_condition_technician" {
  realm_id          = keycloak_realm.fixit.id
  parent_flow_alias = keycloak_authentication_subflow.role_check.alias
  authenticator     = "conditional-user-role"
  requirement       = "REQUIRED"

  depends_on = [keycloak_authentication_execution.role_condition_operator]
}

resource "keycloak_authentication_execution_config" "role_condition_technician" {
  realm_id     = keycloak_realm.fixit.id
  execution_id = keycloak_authentication_execution.role_condition_technician.id
  alias        = "fixit-role-condition-technician"
  config = {
    condUserRole = "fixit-technician"
    negate       = "true"
  }
}

resource "keycloak_authentication_execution" "role_condition_supervisor" {
  realm_id          = keycloak_realm.fixit.id
  parent_flow_alias = keycloak_authentication_subflow.role_check.alias
  authenticator     = "conditional-user-role"
  requirement       = "REQUIRED"

  depends_on = [keycloak_authentication_execution.role_condition_technician]
}

resource "keycloak_authentication_execution_config" "role_condition_supervisor" {
  realm_id     = keycloak_realm.fixit.id
  execution_id = keycloak_authentication_execution.role_condition_supervisor.id
  alias        = "fixit-role-condition-supervisor"
  config = {
    condUserRole = "fixit-supervisor"
    negate       = "true"
  }
}

resource "keycloak_authentication_execution" "deny_no_role" {
  realm_id          = keycloak_realm.fixit.id
  parent_flow_alias = keycloak_authentication_subflow.role_check.alias
  authenticator     = "deny-access-authenticator"
  requirement       = "REQUIRED"

  depends_on = [keycloak_authentication_execution.role_condition_supervisor]
}

# MFA sub-flow — TOTP and WebAuthn offered as ALTERNATIVE (user's choice);
# MFA setup sub-flow ensures new users are prompted to enroll before first use.

resource "keycloak_authentication_subflow" "mfa" {
  realm_id          = keycloak_realm.fixit.id
  parent_flow_alias = keycloak_authentication_flow.fixit_browser.alias
  alias             = "fixit-mfa"
  provider_id       = "basic-flow"
  requirement       = "REQUIRED"

  depends_on = [keycloak_authentication_subflow.role_check]
}

# MFA setup: prompt users with no MFA credential to enroll on first login.

resource "keycloak_authentication_subflow" "mfa_setup" {
  realm_id          = keycloak_realm.fixit.id
  parent_flow_alias = keycloak_authentication_subflow.mfa.alias
  alias             = "fixit-mfa-setup"
  provider_id       = "basic-flow"
  requirement       = "CONDITIONAL"
}

resource "keycloak_authentication_execution" "mfa_setup_condition" {
  realm_id          = keycloak_realm.fixit.id
  parent_flow_alias = keycloak_authentication_subflow.mfa_setup.alias
  authenticator     = "conditional-user-configured"
  requirement       = "REQUIRED"
}

resource "keycloak_authentication_execution" "webauthn_setup" {
  realm_id          = keycloak_realm.fixit.id
  parent_flow_alias = keycloak_authentication_subflow.mfa_setup.alias
  authenticator     = "webauthn-register"
  requirement       = "REQUIRED"

  depends_on = [keycloak_authentication_execution.mfa_setup_condition]
}

# MFA challenge: users with credentials choose TOTP or WebAuthn.

resource "keycloak_authentication_execution" "totp" {
  realm_id          = keycloak_realm.fixit.id
  parent_flow_alias = keycloak_authentication_subflow.mfa.alias
  authenticator     = "auth-otp-form"
  requirement       = "ALTERNATIVE"

  depends_on = [keycloak_authentication_subflow.mfa_setup]
}

resource "keycloak_authentication_execution" "webauthn" {
  realm_id          = keycloak_realm.fixit.id
  parent_flow_alias = keycloak_authentication_subflow.mfa.alias
  authenticator     = "webauthn-authenticator"
  requirement       = "ALTERNATIVE"

  depends_on = [keycloak_authentication_execution.totp]
}

# ---------------------------------------------------------------------------
# Bind the custom browser flow to the realm after the flow exists.
#
# keycloak_realm is created before keycloak_authentication_flow (the flow
# references realm.id, so a direct depends_on would create a cycle). The
# flow alias must exist in Keycloak before the realm update that sets
# browserFlow can succeed. We therefore decouple creation from binding:
# the realm is created without browser_flow, and this resource patches it
# via the Keycloak admin REST API once the flow and all its executions are
# in place. Run `terraform apply` once; a second apply is a no-op.
# ---------------------------------------------------------------------------

resource "terraform_data" "bind_browser_flow" {
  # Triggers re-run if the flow alias changes.
  input = keycloak_authentication_flow.fixit_browser.alias

  depends_on = [
    keycloak_authentication_flow.fixit_browser,
    keycloak_authentication_execution.webauthn,
    keycloak_authentication_execution.deny_no_role,
  ]

  provisioner "local-exec" {
    command = <<-EOT
      TOKEN=$(curl -sf \
        -d "client_id=admin-cli" \
        -d "username=${local.keycloak_admin["username"]}" \
        -d "password=${local.keycloak_admin["password"]}" \
        -d "grant_type=password" \
        "https://auth.fixit.fixcorp.internal/realms/master/protocol/openid-connect/token" \
        | tr ',' '\n' | grep '"access_token"' | cut -d'"' -f4)
      curl -sf -X PUT \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d '{"browserFlow":"fixit-browser"}' \
        "https://auth.fixit.fixcorp.internal/admin/realms/fixit"
    EOT
  }
}

# ---------------------------------------------------------------------------
# Module outputs consumed by sibling modules
# ---------------------------------------------------------------------------

output "keycloak_realm_url" {
  description = "OIDC issuer URL for the fixit realm"
  value       = "https://auth.fixit.fixcorp.internal/realms/fixit"
}

output "oidc_client_id" {
  description = "Client ID of the fixit SPA OIDC client"
  value       = keycloak_openid_client.fixit_spa.client_id
}

output "jwks_uri" {
  description = "JWKS endpoint for validating JWT access tokens issued by Keycloak"
  value       = "https://auth.fixit.fixcorp.internal/realms/fixit/protocol/openid-connect/certs"
}
