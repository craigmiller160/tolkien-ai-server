package us.craigmiller160.tolkienai.server.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "tolkienai.raw-sources")
data class RawSourcesProperties(
    val silmarillion: String
)
