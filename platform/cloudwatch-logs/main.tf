# cloudwatch-logs — Amazon CloudWatch Logs, Amazon S3 (log archive), AWS Glacier, AWS KMS SSE-KMS
# LLD component cmp:cloudwatch-logs. Provision and configure it here; nothing in this module is applied
# until a pull request adds the provider resources and the plan is reviewed.

variable "environment" {
  type = string
}
