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
