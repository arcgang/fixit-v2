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
