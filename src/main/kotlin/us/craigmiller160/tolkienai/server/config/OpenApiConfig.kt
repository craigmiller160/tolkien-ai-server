package us.craigmiller160.tolkienai.server.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.SpecVersion
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
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
                  url = "http://localhost:8080"
                  description = "API Server URL"
                })
      }
}
