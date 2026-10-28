# prometheus-grafana — Prometheus, Grafana, EKS (cluster-internal)
# LLD component cmp:prometheus-grafana. Provision and configure it here; nothing in this module is applied
# until a pull request adds the provider resources and the plan is reviewed.

variable "environment" {
  type = string
}
