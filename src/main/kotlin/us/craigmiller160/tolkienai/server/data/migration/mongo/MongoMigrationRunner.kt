package us.craigmiller160.tolkienai.server.data.migration.mongo

import com.mongodb.client.MongoClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import us.craigmiller160.tolkienai.server.data.migration.Migration
import us.craigmiller160.tolkienai.server.data.migration.mongo.migrations.V001_InitialSchema

@Component
class MongoMigrationRunner(
    private val client: MongoClient,
    private val mongoTemplate: MongoTemplate,
    @Value("\${spring.data.mongodb.database}") private val database: String
) {
  fun run() {
    val db = client.getDatabase(database)
    runMigration(1, V001_InitialSchema(), db)
  }

  private fun <T> runMigration(index: Int, migration: Migration<T>, helper: T) {
    val classLocation = migration.javaClass.protectionDomain.codeSource.location
    //    migration.migrate(helper)
  }
}
