package us.craigmiller160.tolkienai.server.config

import org.springframework.boot.context.properties.ConfigurationProperties

data class MigrationImplementationProperties(val migrationPaths: List<String>)

@ConfigurationProperties(prefix = "spring.data.migrations")
data class MigrationProperties(val mongo: MigrationImplementationProperties)
