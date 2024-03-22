package us.craigmiller160.tolkienai.server.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "tolkienai.weaviate")
data class WeaviateProperties(val host: String, val scheme: String, val key: String)
