resource "keycloak_role" "tolkien_ai_server_access_role_dev" {
  realm_id = data.keycloak_realm.apps_dev.id
  client_id = keycloak_openid_client.tolkien_ai_server_dev.id
  name = local.access_role_common.name
}

resource "keycloak_role" "tolkien_ai_server_access_role_prod" {
  realm_id = data.keycloak_realm.apps_prod.id
  client_id = keycloak_openid_client.tolkien_ai_server_prod.id
  name = local.access_role_common.name
}

resource "keycloak_role" "tolkien_ai_server_admin_role_dev" {
  realm_id = data.keycloak_realm.apps_dev.id
  client_id = keycloak_openid_client.tolkien_ai_server_dev.id
  name = local.admin_role_common.name
}

resource "keycloak_role" "tolkien_ai_server_admin_role_prod" {
  realm_id = data.keycloak_realm.apps_prod.id
  client_id = keycloak_openid_client.tolkien_ai_server_prod.id
  name = local.admin_role_common.name
}