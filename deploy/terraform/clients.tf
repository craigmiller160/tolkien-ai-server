data "keycloak_realm" "apps_dev" {
  realm = "apps-dev"
}

data "keycloak_realm" "apps_prod" {
  realm = "apps-prod"
}

locals {
  client_common = {
    client_id = "tolkien-ai-server"
    name = "tolkien-ai-server"
    enabled = true
    access_type = "CONFIDENTIAL"
    service_accounts_enabled = true
  }

  access_role_common = {
    name = "access"
  }

  admin_role_common = {
    name = "admin"
  }
}

resource "keycloak_openid_client" "tolkien_ai_server_dev" {
  realm_id = data.keycloak_realm.apps_dev.id
  client_id = local.client_common.client_id
  name = local.client_common.name
  enabled = local.client_common.enabled
  access_type = local.client_common.access_type
  service_accounts_enabled = local.client_common.service_accounts_enabled
}

resource "keycloak_openid_client" "tolkien_ai_server_prod" {
  realm_id = data.keycloak_realm.apps_prod.id
  client_id = local.client_common.client_id
  name = local.client_common.name
  enabled = local.client_common.enabled
  access_type = local.client_common.access_type
  service_accounts_enabled = local.client_common.service_accounts_enabled
}