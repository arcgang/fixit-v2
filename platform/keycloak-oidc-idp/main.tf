# keycloak-oidc-idp — Keycloak 24+, OIDC, SAML 2.0, EKS Pod (HA)
# LLD component cmp:keycloak-oidc-idp. Provision and configure it here; nothing in this module is applied
# until a pull request adds the provider resources and the plan is reviewed.

variable "environment" {
  type = string
}
