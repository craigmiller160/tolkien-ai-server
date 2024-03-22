package us.craigmiller160.tolkienai.server.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource(
    ignoreResourceNotFound = true,
    value = ["file:./secrets.yml"],
    factory = YamlPropertySourceFactory::class)
class SecretsConfig
