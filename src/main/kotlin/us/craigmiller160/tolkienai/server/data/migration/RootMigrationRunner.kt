package us.craigmiller160.tolkienai.server.data.migration

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import us.craigmiller160.tolkienai.server.config.MigrationProperties
import us.craigmiller160.tolkienai.server.data.migration.mongo.MongoMigrationRunner

@Component
class RootMigrationRunner(
    private val mongoMigrationRunner: MongoMigrationRunner,
    private val migrationProperties: MigrationProperties,
) : MigrationRunner {
  private val log = LoggerFactory.getLogger(javaClass)
  @PostConstruct
  override fun run(): List<MigrationReport> {
    if (!migrationProperties.enabled) {
      log.info("Data migration is disabled, not running migrations")
      return listOf()
    }

    log.info("Finding and running migrations")
    return mongoMigrationRunner.run().also { log.info("All migrations completed") }
  }
}
