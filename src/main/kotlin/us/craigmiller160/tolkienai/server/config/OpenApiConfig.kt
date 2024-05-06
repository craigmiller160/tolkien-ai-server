package us.craigmiller160.tolkienai.server.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.SpecVersion
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig(
    @Value("\${openapi.server}") private val server: String,
    @Value("\${spring.security.keycloak.oauth2.resourceserver.host}")
    private val authServerUrl: String,
    @Value("\${spring.security.keycloak.oauth2.resourceserver.realm}") private val realm: String,
    @Value("\${spring.security.keycloak.oauth2.resourceserver.client-id}")
    private val clientId: String
) {
  @Bean
  fun openApi(): OpenAPI =
      OpenAPI().apply {
        specVersion = SpecVersion.V31
        info = createInfo()

        servers = createServers(server)

        components =
            Components()
                .addSecuritySchemes(
                    "OAuth2Password", createSecurityScheme(authServerUrl, realm, clientId))
      }
}

private fun createSecurityScheme(
    authServerUrl: String,
    realm: String,
    clientId: String
): SecurityScheme =
    SecurityScheme().apply {
      type = SecurityScheme.Type.OAUTH2
      flows =
          OAuthFlows().apply {
            password =
                OAuthFlow().apply {
                  tokenUrl = "$authServerUrl/realms/$realm/protocol/openid-connect/token"
                }
          }
    }

private fun createServers(server: String): List<Server> =
    Server()
        .apply {
          url = server
          description = "API Server URL"
        }
        .let { listOf(it) }

private fun createInfo(): Info =
    Info().apply {
      title = "Tolkien AI Server"
      version = "v1"
    }
