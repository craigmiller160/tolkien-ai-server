package us.craigmiller160.tolkienai.server.testcore

import com.aallam.openai.client.OpenAI
import io.weaviate.client.WeaviateClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("test")
@Configuration
class MockConfig {
  @Bean fun weaviateClient(): WeaviateClient = TODO()

  @Bean fun openaiClient(): OpenAI = TODO()
}
