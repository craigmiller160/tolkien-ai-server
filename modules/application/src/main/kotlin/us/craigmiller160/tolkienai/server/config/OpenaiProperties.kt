package us.craigmiller160.tolkienai.server.config

import org.springframework.boot.context.properties.ConfigurationProperties

data class OpenaiChatModelProperties(val name: String)

data class OpenaiEmbeddingModelProperties(val name: String, val dimensions: Int)

data class OpenaiModelProperties(
    val embedding: OpenaiEmbeddingModelProperties,
    val chat: OpenaiChatModelProperties
)

data class OpenaiTimeoutProperties(
    val requestSeconds: Int,
    val socketSeconds: Int,
    val connectSeconds: Int
)

@ConfigurationProperties(prefix = "tolkienai.openai")
data class OpenaiProperties(
    val key: String,
    val timeouts: OpenaiTimeoutProperties,
    val models: OpenaiModelProperties
)
