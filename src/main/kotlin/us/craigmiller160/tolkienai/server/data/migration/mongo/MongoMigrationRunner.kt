package us.craigmiller160.tolkienai.server.data.migration.mongo

import com.mongodb.client.MongoClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import us.craigmiller160.tolkienai.server.config.MigrationProperties
import us.craigmiller160.tolkienai.server.data.migration.AbstractMigrationImplementationRunner
import us.craigmiller160.tolkienai.server.data.migration.RegisteredMigration
import us.craigmiller160.tolkienai.server.data.migration.mongo.migrations.V001_InitialSchema

@Component
class MongoMigrationRunner(
    client: MongoClient,
    mongoTemplate: MongoTemplate,
    migrationProperties: MigrationProperties,
    @Value("\${spring.data.mongodb.database}") private val database: String
) :
    AbstractMigrationImplementationRunner<MongoMigrationHelper>(
        mongoTemplate, migrationProperties.mongo) {
  private val helper =
      MongoMigrationHelper(database = client.getDatabase(database), template = mongoTemplate)

  override val registeredMigrations: List<RegisteredMigration<MongoMigrationHelper>> =
      listOf(RegisteredMigration(migration = V001_InitialSchema(), helper = helper))
  override val collectionName: String = "mongo_migration_history"
}
