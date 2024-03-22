package us.craigmiller160.tolkienai.server.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "tolkienai.openai")
data class OpenaiProperties(
    val key: String
)
