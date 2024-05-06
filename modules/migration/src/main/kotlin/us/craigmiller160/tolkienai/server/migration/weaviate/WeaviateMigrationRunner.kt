package us.craigmiller160.tolkienai.server.migration.weaviate

import io.weaviate.client.WeaviateClient
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import us.craigmiller160.tolkienai.server.config.MigrationProperties
import us.craigmiller160.tolkienai.server.config.WeaviateProperties
import us.craigmiller160.tolkienai.server.migration.AbstractMigrationImplementationRunner

@Component
class WeaviateMigrationRunner(
    weaviateClient: WeaviateClient,
    mongoTemplate: MongoTemplate,
    migrationProperties: MigrationProperties,
    weaviateProperties: WeaviateProperties
) :
    us.craigmiller160.tolkienai.server.migration.AbstractMigrationImplementationRunner<
        WeaviateMigrationHelper>(mongoTemplate, migrationProperties.weaviate) {
  override val collectionName: String = "weaviate_migration_history"
  override val helper: WeaviateMigrationHelper =
      WeaviateMigrationHelper(client = weaviateClient, className = weaviateProperties.className)
}
