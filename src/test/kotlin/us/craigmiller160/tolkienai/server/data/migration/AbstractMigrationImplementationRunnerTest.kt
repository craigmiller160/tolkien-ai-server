package us.craigmiller160.tolkienai.server.data.migration

import org.junit.jupiter.api.Test
import org.springframework.data.mongodb.core.MongoTemplate

class AbstractMigrationImplementationRunnerTest {
  @Test
  fun `performs migration`() {

    /*
     * 1) Successful new migrations
     * 2) No new migrations to perform
     * 3) Migration at index with invalid name
     * 4) Migration at index with invalid hash
     */
    TODO()
  }
}

class TestMigrationImplementationRunner(
    mongoTemplate: MongoTemplate,
    override val registeredMigrations: List<RegisteredMigration<*>>,
    override val collectionName: String
) : AbstractMigrationImplementationRunner(mongoTemplate)
