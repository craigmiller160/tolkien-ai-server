package us.craigmiller160.tolkienai.server.config

import org.springframework.boot.context.properties.ConfigurationProperties

data class SourceProperties(val path: String, val excludeLines: List<String>)

@ConfigurationProperties(prefix = "tolkienai.raw-sources")
data class RawSourcesProperties(val silmarillion: SourceProperties)
