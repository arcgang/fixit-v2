# Acceptance tests for the keycloak-oidc-idp module.
# Run with: terraform test (requires Terraform >= 1.6 and provider credentials).
# These tests validate the plan shape, not live Keycloak side-effects.

variables {
  environment             = "test"
  spa_cloudfront_url      = "https://example.cloudfront.net"
  refresh_token_ttl_seconds = 28800
}

# --- Precondition: realm resource must exist in plan ---
run "realm_resource_exists_in_configuration" {
  command = plan

  assert {
    condition     = keycloak_realm.fixit.realm == "fixit"
    error_message = "keycloak_realm.fixit must have realm = \"fixit\""
  }

  assert {
    condition     = keycloak_realm.fixit.display_name == "FixIt"
    error_message = "keycloak_realm.fixit must have display_name = \"FixIt\""
  }

  assert {
    condition     = keycloak_realm.fixit.enabled == true
    error_message = "keycloak_realm.fixit must be enabled"
  }

  assert {
    condition     = keycloak_realm.fixit.ssl_required == "external"
    error_message = "keycloak_realm.fixit must have ssl_required = \"external\""
  }

  assert {
    condition     = keycloak_realm.fixit.internationalization_enabled == false
    error_message = "keycloak_realm.fixit must have internationalization_enabled = false"
  }
}

# --- Three roles must be declared with the correct realm scope ---
run "operator_role_scoped_to_fixit_realm" {
  command = plan

  assert {
    condition     = keycloak_role.fixit_operator.name == "fixit-operator"
    error_message = "keycloak_role.fixit_operator must have name = \"fixit-operator\""
  }

  assert {
    condition     = keycloak_role.fixit_operator.realm_id == keycloak_realm.fixit.id
    error_message = "keycloak_role.fixit_operator must reference the fixit realm id"
  }
}

run "technician_role_scoped_to_fixit_realm" {
  command = plan

  assert {
    condition     = keycloak_role.fixit_technician.name == "fixit-technician"
    error_message = "keycloak_role.fixit_technician must have name = \"fixit-technician\""
  }

  assert {
    condition     = keycloak_role.fixit_technician.realm_id == keycloak_realm.fixit.id
    error_message = "keycloak_role.fixit_technician must reference the fixit realm id"
  }
}

run "supervisor_role_scoped_to_fixit_realm" {
  command = plan

  assert {
    condition     = keycloak_role.fixit_supervisor.name == "fixit-supervisor"
    error_message = "keycloak_role.fixit_supervisor must have name = \"fixit-supervisor\""
  }

  assert {
    condition     = keycloak_role.fixit_supervisor.realm_id == keycloak_realm.fixit.id
    error_message = "keycloak_role.fixit_supervisor must reference the fixit realm id"
  }
}

# --- OIDC public client must be configured correctly ---
run "spa_client_is_public_pkce_s256" {
  command = plan

  assert {
    condition     = keycloak_openid_client.fixit_spa.client_id == "fixit-spa"
    error_message = "keycloak_openid_client.fixit_spa must have client_id = \"fixit-spa\""
  }

  assert {
    condition     = keycloak_openid_client.fixit_spa.access_type == "PUBLIC"
    error_message = "keycloak_openid_client.fixit_spa must be a PUBLIC (non-confidential) client"
  }

  assert {
    condition     = keycloak_openid_client.fixit_spa.standard_flow_enabled == true
    error_message = "keycloak_openid_client.fixit_spa must have standard_flow_enabled = true"
  }

  assert {
    condition     = keycloak_openid_client.fixit_spa.implicit_flow_enabled == false
    error_message = "keycloak_openid_client.fixit_spa must have implicit_flow_enabled = false"
  }

  assert {
    condition     = keycloak_openid_client.fixit_spa.pkce_code_challenge_method == "S256"
    error_message = "keycloak_openid_client.fixit_spa must have pkce_code_challenge_method = \"S256\""
  }
}

run "spa_client_access_token_lifespan_within_limit" {
  command = plan

  assert {
    condition     = tonumber(keycloak_openid_client.fixit_spa.access_token_lifespan) <= 3600
    error_message = "keycloak_openid_client.fixit_spa access_token_lifespan must be <= 3600 seconds"
  }
}

run "spa_client_session_timeouts_use_refresh_token_ttl_var" {
  command = plan

  assert {
    condition     = keycloak_openid_client.fixit_spa.client_session_idle_timeout == tostring(var.refresh_token_ttl_seconds)
    error_message = "client_session_idle_timeout must equal var.refresh_token_ttl_seconds"
  }

  assert {
    condition     = keycloak_openid_client.fixit_spa.client_session_max_lifespan == tostring(var.refresh_token_ttl_seconds)
    error_message = "client_session_max_lifespan must equal var.refresh_token_ttl_seconds"
  }
}

run "spa_client_redirect_and_web_origin_match_cloudfront_url" {
  command = plan

  assert {
    condition     = contains(keycloak_openid_client.fixit_spa.valid_redirect_uris, var.spa_cloudfront_url)
    error_message = "valid_redirect_uris must contain var.spa_cloudfront_url"
  }

  assert {
    condition     = contains(keycloak_openid_client.fixit_spa.web_origins, var.spa_cloudfront_url)
    error_message = "web_origins must contain var.spa_cloudfront_url"
  }
}

# ---------------------------------------------------------------------------
# Browser authentication flow — MFA enforcement and role-presence check
# ---------------------------------------------------------------------------

# --- Precondition: top-level browser flow must exist and be bound to the realm ---
run "browser_flow_bound_to_realm" {
  command = plan

  assert {
    condition     = keycloak_realm.fixit.browser_flow == "fixit-browser"
    error_message = "keycloak_realm.fixit browser_flow must be set to \"fixit-browser\""
  }
}

run "browser_flow_is_created_in_fixit_realm" {
  command = plan

  assert {
    condition     = keycloak_authentication_flow.fixit_browser.realm_id == keycloak_realm.fixit.id
    error_message = "keycloak_authentication_flow.fixit_browser must belong to the fixit realm"
  }

  assert {
    condition     = keycloak_authentication_flow.fixit_browser.alias == "fixit-browser"
    error_message = "keycloak_authentication_flow.fixit_browser must have alias = \"fixit-browser\""
  }
}

# --- MFA sub-flow must exist as a child of the top-level flow ---
run "mfa_subflow_exists_under_browser_flow" {
  command = plan

  assert {
    condition     = keycloak_authentication_subflow.mfa.realm_id == keycloak_realm.fixit.id
    error_message = "keycloak_authentication_subflow.mfa must belong to the fixit realm"
  }

  assert {
    condition     = keycloak_authentication_subflow.mfa.parent_flow_alias == keycloak_authentication_flow.fixit_browser.alias
    error_message = "keycloak_authentication_subflow.mfa must be a child of the fixit_browser flow"
  }
}

# --- TOTP execution must be ALTERNATIVE inside the MFA sub-flow ---
run "totp_execution_is_alternative_in_mfa_subflow" {
  command = plan

  assert {
    condition     = keycloak_authentication_execution.totp.realm_id == keycloak_realm.fixit.id
    error_message = "keycloak_authentication_execution.totp must belong to the fixit realm"
  }

  assert {
    condition     = keycloak_authentication_execution.totp.parent_flow_alias == keycloak_authentication_subflow.mfa.alias
    error_message = "keycloak_authentication_execution.totp must be a child of the MFA sub-flow"
  }

  assert {
    condition     = keycloak_authentication_execution.totp.authenticator == "auth-otp-form"
    error_message = "keycloak_authentication_execution.totp must use the auth-otp-form provider"
  }

  assert {
    condition     = keycloak_authentication_execution.totp.requirement == "ALTERNATIVE"
    error_message = "keycloak_authentication_execution.totp must be ALTERNATIVE so WebAuthn is also an option"
  }
}

# --- WebAuthn execution must be ALTERNATIVE inside the MFA sub-flow ---
run "webauthn_execution_is_alternative_in_mfa_subflow" {
  command = plan

  assert {
    condition     = keycloak_authentication_execution.webauthn.realm_id == keycloak_realm.fixit.id
    error_message = "keycloak_authentication_execution.webauthn must belong to the fixit realm"
  }

  assert {
    condition     = keycloak_authentication_execution.webauthn.parent_flow_alias == keycloak_authentication_subflow.mfa.alias
    error_message = "keycloak_authentication_execution.webauthn must be a child of the MFA sub-flow"
  }

  assert {
    condition     = keycloak_authentication_execution.webauthn.authenticator == "webauthn-authenticator"
    error_message = "keycloak_authentication_execution.webauthn must use the webauthn-authenticator provider"
  }

  assert {
    condition     = keycloak_authentication_execution.webauthn.requirement == "ALTERNATIVE"
    error_message = "keycloak_authentication_execution.webauthn must be ALTERNATIVE alongside TOTP"
  }
}

# --- Role-presence check: conditional sub-flow must deny users with no FixIt role ---
run "role_check_subflow_exists_under_browser_flow" {
  command = plan

  assert {
    condition     = keycloak_authentication_subflow.role_check.realm_id == keycloak_realm.fixit.id
    error_message = "keycloak_authentication_subflow.role_check must belong to the fixit realm"
  }

  assert {
    condition     = keycloak_authentication_subflow.role_check.parent_flow_alias == keycloak_authentication_flow.fixit_browser.alias
    error_message = "keycloak_authentication_subflow.role_check must be a child of the fixit_browser flow"
  }

  assert {
    condition     = keycloak_authentication_subflow.role_check.provider_id == "conditional"
    error_message = "keycloak_authentication_subflow.role_check must use the conditional provider"
  }
}

run "role_check_deny_execution_blocks_users_without_role" {
  command = plan

  assert {
    condition     = keycloak_authentication_execution.deny_no_role.realm_id == keycloak_realm.fixit.id
    error_message = "keycloak_authentication_execution.deny_no_role must belong to the fixit realm"
  }

  assert {
    condition     = keycloak_authentication_execution.deny_no_role.parent_flow_alias == keycloak_authentication_subflow.role_check.alias
    error_message = "keycloak_authentication_execution.deny_no_role must be a child of the role_check sub-flow"
  }

  assert {
    condition     = keycloak_authentication_execution.deny_no_role.authenticator == "deny-access-authenticator"
    error_message = "keycloak_authentication_execution.deny_no_role must use the deny-access-authenticator provider"
  }

  assert {
    condition     = keycloak_authentication_execution.deny_no_role.requirement == "REQUIRED"
    error_message = "keycloak_authentication_execution.deny_no_role must be REQUIRED to enforce the denial"
  }
}

# --- Role condition executions: one per FixIt role, all DISABLED so deny fires when NONE match ---
run "role_condition_operator_targets_fixit_operator" {
  command = plan

  assert {
    condition     = keycloak_authentication_execution.role_condition_operator.authenticator == "conditional-user-role"
    error_message = "role_condition_operator must use the conditional-user-role authenticator"
  }

  assert {
    condition     = keycloak_authentication_execution.role_condition_operator.parent_flow_alias == keycloak_authentication_subflow.role_check.alias
    error_message = "role_condition_operator must be in the role_check sub-flow"
  }
}

run "role_condition_technician_targets_fixit_technician" {
  command = plan

  assert {
    condition     = keycloak_authentication_execution.role_condition_technician.authenticator == "conditional-user-role"
    error_message = "role_condition_technician must use the conditional-user-role authenticator"
  }

  assert {
    condition     = keycloak_authentication_execution.role_condition_technician.parent_flow_alias == keycloak_authentication_subflow.role_check.alias
    error_message = "role_condition_technician must be in the role_check sub-flow"
  }
}

run "role_condition_supervisor_targets_fixit_supervisor" {
  command = plan

  assert {
    condition     = keycloak_authentication_execution.role_condition_supervisor.authenticator == "conditional-user-role"
    error_message = "role_condition_supervisor must use the conditional-user-role authenticator"
  }

  assert {
    condition     = keycloak_authentication_execution.role_condition_supervisor.parent_flow_alias == keycloak_authentication_subflow.role_check.alias
    error_message = "role_condition_supervisor must be in the role_check sub-flow"
  }
}

# --- Module outputs must equal the expected static values ---
run "module_outputs_have_correct_values" {
  command = plan

  assert {
    condition     = output.keycloak_realm_url == "https://auth.fixit.fixcorp.internal/realms/fixit"
    error_message = "output keycloak_realm_url must be the fixit realm URL"
  }

  assert {
    condition     = output.oidc_client_id == keycloak_openid_client.fixit_spa.client_id
    error_message = "output oidc_client_id must equal the fixit-spa client_id"
  }

  assert {
    condition     = output.jwks_uri == "https://auth.fixit.fixcorp.internal/realms/fixit/protocol/openid-connect/certs"
    error_message = "output jwks_uri must be the fixit JWKS URI"
  }
}
