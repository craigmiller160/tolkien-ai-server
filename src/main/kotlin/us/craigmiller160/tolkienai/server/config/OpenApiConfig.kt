package us.craigmiller160.tolkienai.server.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.SpecVersion
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig(@Value("\${openapi.server}") private val server: String) {
  @Bean
  fun openApi(): OpenAPI =
      OpenAPI().apply {
        info =
            Info().apply {
              title = "Tolkien AI Server"
              version = "v1"
              specVersion = SpecVersion.V31
            }

        servers =
            listOf(
                Server().apply {
                  url = server
                  description = "API Server URL"
                })
      }
}
