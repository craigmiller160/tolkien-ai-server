package us.craigmiller160.tolkienai.server.testcore

import com.aallam.openai.client.OpenAI
import io.mockk.mockk
import io.weaviate.client.WeaviateClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("test")
@Configuration
class MockConfig {
  @Bean fun weaviateClient(): WeaviateClient = mockk<WeaviateClient>()

  @Bean fun openaiClient(): OpenAI = mockk<OpenAI>()
}
