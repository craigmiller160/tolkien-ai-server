package us.craigmiller160.tolkienai.server.data.migration

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import us.craigmiller160.tolkienai.server.data.migration.mongo.MongoMigrationRunner

@Component
class RootMigrationRunner(private val mongoMigrationRunner: MongoMigrationRunner) :
    MigrationRunner {
  private val log = LoggerFactory.getLogger(javaClass)
  @PostConstruct
  override fun run() {
    log.info("Finding and running database migrations")
    mongoMigrationRunner.run()
  }
}
