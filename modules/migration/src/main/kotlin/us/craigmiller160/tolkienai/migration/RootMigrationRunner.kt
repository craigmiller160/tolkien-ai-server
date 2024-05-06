package us.craigmiller160.tolkienai.migration

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import us.craigmiller160.tolkienai.migration.config.MigrationProperties
import us.craigmiller160.tolkienai.migration.mongo.MongoMigrationRunner
import us.craigmiller160.tolkienai.migration.weaviate.WeaviateMigrationRunner

@Component
class RootMigrationRunner(
    private val mongoMigrationRunner: MongoMigrationRunner,
    private val weaviateMigrationRunner: WeaviateMigrationRunner,
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
    val mongoReports = mongoMigrationRunner.run()
    val weaviateReports = weaviateMigrationRunner.run()

    log.info("All migrations completed")
    return mongoReports + weaviateReports
  }
}
