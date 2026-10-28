module "aws_api_gateway" {
  source      = "./aws-api-gateway"
  environment = var.environment
}

module "aurora_postgresql" {
  source      = "./aurora-postgresql"
  environment = var.environment
}

module "aws_secrets_manager" {
  source      = "./aws-secrets-manager"
  environment = var.environment
}

module "cloudwatch_logs" {
  source      = "./cloudwatch-logs"
  environment = var.environment
}
