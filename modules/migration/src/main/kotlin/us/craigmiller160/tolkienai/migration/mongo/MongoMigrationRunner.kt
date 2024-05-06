package us.craigmiller160.tolkienai.migration.mongo

import com.mongodb.client.MongoClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import us.craigmiller160.tolkienai.migration.AbstractMigrationImplementationRunner
import us.craigmiller160.tolkienai.migration.config.MigrationProperties

@Component
class MongoMigrationRunner(
    client: MongoClient,
    mongoTemplate: MongoTemplate,
    migrationProperties: MigrationProperties,
    @Value("\${spring.data.mongodb.database}") private val database: String
) :
    AbstractMigrationImplementationRunner<MongoMigrationHelper>(
        mongoTemplate, migrationProperties.mongo) {
  override val helper =
      MongoMigrationHelper(database = client.getDatabase(database), template = mongoTemplate)

  override val collectionName: String = "mongo_migration_history"
}
