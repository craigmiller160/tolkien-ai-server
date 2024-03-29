package us.craigmiller160.tolkienai.server.config

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import io.weaviate.client.Config
import io.weaviate.client.WeaviateAuthClient
import io.weaviate.client.WeaviateClient
import kotlin.time.Duration.Companion.seconds
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ClientConfig {

  @Bean
  fun weaviateClient(props: WeaviateProperties): WeaviateClient =
      Config(props.scheme, props.host).let { WeaviateAuthClient.apiKey(it, props.key) }

  @Bean
  fun openaiClient(props: OpenaiProperties): OpenAI =
      OpenAI(
          token = props.key,
          timeout = props.timeouts.toTimeout(),
          logging = LoggingConfig(logLevel = LogLevel.None),
      )
}

private fun OpenaiTimeoutProperties.toTimeout(): Timeout =
    Timeout(
        socket = this.socketSeconds.seconds,
        connect = this.connectSeconds.seconds,
        request = this.requestSeconds.seconds)
