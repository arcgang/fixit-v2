# amazon-aurora-postgresql — Amazon Aurora PostgreSQL 15, Multi-AZ, AWS KMS CMK
# LLD component cmp:amazon-aurora-postgresql. Provision and configure it here; nothing in this module is applied
# until a pull request adds the provider resources and the plan is reviewed.

variable "environment" {
  type = string
}
