variable "spa_cloudfront_url" {
  type        = string
  description = "HTTPS CloudFront distribution URL for the FixIt SPA"
}

variable "refresh_token_ttl_seconds" {
  type        = number
  default     = 28800
  description = "Idle and max session lifespan in seconds for the SPA OIDC client (default 8 h)"
}

module "aws_api_gateway" {
  source      = "./aws-api-gateway"
  environment = var.environment
}

module "keycloak_oidc_idp" {
  source                    = "./keycloak-oidc-idp"
  environment               = var.environment
  spa_cloudfront_url        = var.spa_cloudfront_url
  refresh_token_ttl_seconds = var.refresh_token_ttl_seconds
}

module "amazon_aurora_postgresql" {
  source      = "./amazon-aurora-postgresql"
  environment = var.environment
}

module "aws_secrets_manager" {
  source      = "./aws-secrets-manager"
  environment = var.environment
}

module "aws_cloudwatch_logs" {
  source      = "./aws-cloudwatch-logs"
  environment = var.environment
}

module "prometheus_grafana" {
  source      = "./prometheus-grafana"
  environment = var.environment
}

module "aws_kms" {
  source      = "./aws-kms"
  environment = var.environment
}

module "amazon_s3" {
  source      = "./amazon-s3"
  environment = var.environment
}

# ---------------------------------------------------------------------------
# Root outputs: expose keycloak_oidc_idp outputs for sibling modules
# ---------------------------------------------------------------------------

output "keycloak_realm_url" {
  description = "OIDC issuer URL for the fixit realm"
  value       = module.keycloak_oidc_idp.keycloak_realm_url
}

output "oidc_client_id" {
  description = "Client ID of the fixit SPA OIDC client"
  value       = module.keycloak_oidc_idp.oidc_client_id
}

output "jwks_uri" {
  description = "JWKS endpoint for validating JWT access tokens"
  value       = module.keycloak_oidc_idp.jwks_uri
}
