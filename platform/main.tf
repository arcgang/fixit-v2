module "aws_api_gateway" {
  source      = "./aws-api-gateway"
  environment = var.environment
}

module "keycloak_oidc_idp" {
  source      = "./keycloak-oidc-idp"
  environment = var.environment
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
