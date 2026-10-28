# aws-secrets-manager — AWS Secrets Manager, AWS KMS
# LLD component cmp:aws-secrets-manager. Provision and configure it here; nothing in this module is applied
# until a pull request adds the provider resources and the plan is reviewed.

variable "environment" {
  type = string
}
