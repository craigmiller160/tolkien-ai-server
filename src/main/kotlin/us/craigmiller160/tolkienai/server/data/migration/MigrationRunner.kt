package us.craigmiller160.tolkienai.server.data.migration

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import us.craigmiller160.tolkienai.server.data.migration.mongo.MongoMigrationRunner

@Component
class MigrationRunner(private val mongoMigrationRunner: MongoMigrationRunner) {
  @PostConstruct
  fun run() {
    mongoMigrationRunner.run()
  }
}
