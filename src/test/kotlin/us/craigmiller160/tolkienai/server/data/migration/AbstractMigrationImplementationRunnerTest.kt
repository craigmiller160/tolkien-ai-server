package us.craigmiller160.tolkienai.server.data.migration

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Query
import us.craigmiller160.tolkienai.server.data.migration.other.MockMigration

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
    val migrations = listOf(MockMigration(), MockMigration(), MockMigration())

    val historyRecords =
        migrations.take(1).mapIndexed { index, migration ->
          MigrationHistoryRecord(
              index = index + 1,
              name = migration.javaClass.name,
              hash = generateMigrationHash(migration))
        }
    val registeredMigrations =
        migrations.map { migration -> RegisteredMigration(migration = migration, helper = "Hello") }

    val mongoTemplate = mockk<MongoTemplate>(relaxUnitFun = true)
    val querySlot = slot<Query>()
    every {
      mongoTemplate.find(
          capture(querySlot), MigrationHistoryRecord::class.java, HISTORY_COLLECTION_NAME)
    } returns historyRecords

    every {
      mongoTemplate.insert(any(MigrationHistoryRecord::class), HISTORY_COLLECTION_NAME)
    } answers { arg(0) }

    val runner =
        TestMigrationImplementationRunner(
                mongoTemplate, registeredMigrations, HISTORY_COLLECTION_NAME)
            .also { it.run() }

    migrations[0].didMigrate.shouldBe(false)
    migrations[1].didMigrate.shouldBe(true)
    migrations[2].didMigrate.shouldBe(true)

    val expectedQuery = Query().with(Sort.by(Sort.Direction.ASC, "index"))
    querySlot.captured.shouldBe(expectedQuery)

    // TODO need to verify the inserts
  }
}

class TestMigrationImplementationRunner(
    mongoTemplate: MongoTemplate,
    override val registeredMigrations: List<RegisteredMigration<*>>,
    override val collectionName: String
) : AbstractMigrationImplementationRunner(mongoTemplate)
