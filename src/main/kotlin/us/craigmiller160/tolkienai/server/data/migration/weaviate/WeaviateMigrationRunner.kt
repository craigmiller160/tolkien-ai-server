package us.craigmiller160.tolkienai.server.data.migration.weaviate

import io.weaviate.client.WeaviateClient
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import us.craigmiller160.tolkienai.server.config.MigrationProperties
import us.craigmiller160.tolkienai.server.data.migration.AbstractMigrationImplementationRunner

@Component
class WeaviateMigrationRunner(
    weaviateClient: WeaviateClient,
    mongoTemplate: MongoTemplate,
    migrationProperties: MigrationProperties
) :
    AbstractMigrationImplementationRunner<WeaviateClient>(
        mongoTemplate, migrationProperties.weaviate) {
  override val collectionName: String = "weaviate_migration_history"
  override val helper: WeaviateClient = weaviateClient
}
