# aurora-postgresql — Amazon Aurora PostgreSQL 15, AWS KMS CMK (fixit-aurora-cmk), Cross-region read replica (eu-central-1)
# LLD component cmp:aurora-postgresql. Provision and configure it here; nothing in this module is applied
# until a pull request adds the provider resources and the plan is reviewed.

variable "environment" {
  type = string
}
