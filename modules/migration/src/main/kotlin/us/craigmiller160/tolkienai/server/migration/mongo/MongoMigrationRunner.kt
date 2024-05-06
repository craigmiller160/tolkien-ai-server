package us.craigmiller160.tolkienai.server.migration.mongo

import com.mongodb.client.MongoClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import us.craigmiller160.tolkienai.server.config.MigrationProperties
import us.craigmiller160.tolkienai.server.migration.AbstractMigrationImplementationRunner

@Component
class MongoMigrationRunner(
    client: MongoClient,
    mongoTemplate: MongoTemplate,
    migrationProperties: MigrationProperties,
    @Value("\${spring.data.mongodb.database}") private val database: String
) :
    us.craigmiller160.tolkienai.server.migration.AbstractMigrationImplementationRunner<
        MongoMigrationHelper>(mongoTemplate, migrationProperties.mongo) {
  override val helper =
      MongoMigrationHelper(database = client.getDatabase(database), template = mongoTemplate)

  override val collectionName: String = "mongo_migration_history"
}
