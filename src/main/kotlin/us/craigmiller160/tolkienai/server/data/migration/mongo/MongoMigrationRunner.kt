package us.craigmiller160.tolkienai.server.data.migration.mongo

import com.mongodb.client.MongoClient
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import us.craigmiller160.tolkienai.server.data.migration.Migration

@Component
class MongoMigrationRunner(
    private val client: MongoClient,
    private val mongoTemplate: MongoTemplate
) {
  fun run() {

    TODO()
  }

  private fun <T> runMigration(index: Int, migration: Migration<T>, helper: T) {
    migration.migrate(helper)
  }
}
