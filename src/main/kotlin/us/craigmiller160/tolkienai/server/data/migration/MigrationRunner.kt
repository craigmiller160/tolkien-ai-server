package us.craigmiller160.tolkienai.server.data.migration

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import us.craigmiller160.tolkienai.server.data.migration.mongo.MongoMigrationRunner

@Component
class MigrationRunner(private val mongoMigrationRunner: MongoMigrationRunner) {
  private val log = LoggerFactory.getLogger(javaClass)
  @PostConstruct
  fun run() {
    log.info("Finding and running database migrations")
    mongoMigrationRunner.run()
  }
}
