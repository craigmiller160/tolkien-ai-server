package us.craigmiller160.tolkienai.server.data.migration

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Test
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Query

class AbstractMigrationImplementationRunnerTest {
  companion object {
    private const val HISTORY_COLLECTION_NAME = "history_collection"
  }
  /*
   * 1) Successful new migrations
   * 2) No new migrations to perform
   * 3) Migration at index with invalid name
   * 4) Migration at index with invalid hash
   */

  @Test
  fun `performs migration`() {
    val baseHistoryRecord =
        MigrationHistoryRecord(index = 0, name = MockMigration::class.java.name, hash = "123")
    //    val historyRecords = listOf()

    val mongoTemplate = mockk<MongoTemplate>()
    val querySlot = slot<Query>()
    every {
      mongoTemplate.find(
          capture(querySlot), MigrationHistoryRecord::class.java, HISTORY_COLLECTION_NAME)
    } returns listOf()
  }
}

class TestMigrationImplementationRunner(
    mongoTemplate: MongoTemplate,
    override val registeredMigrations: List<RegisteredMigration<*>>,
    override val collectionName: String
) : AbstractMigrationImplementationRunner(mongoTemplate)

class MockMigration : Migration<String> {
  var didMigrate: Boolean = false
  override fun migrate(helper: String) {
    didMigrate = true
  }
}
