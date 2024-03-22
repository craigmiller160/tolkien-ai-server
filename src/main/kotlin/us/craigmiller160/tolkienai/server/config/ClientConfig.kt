package us.craigmiller160.tolkienai.server.config

import io.weaviate.client.Config
import io.weaviate.client.WeaviateAuthClient
import io.weaviate.client.WeaviateClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ClientConfig {
    @Bean
    fun weaviateClient(props: WeaviateProperties): WeaviateClient {
        val config = Config(props.scheme, props.host)
        return WeaviateAuthClient.apiKey(config, props.key)
    }
}