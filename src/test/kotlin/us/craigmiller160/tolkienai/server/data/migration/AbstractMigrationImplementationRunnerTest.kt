package us.craigmiller160.tolkienai.server.data.migration

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.stream.Stream
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import us.craigmiller160.tolkienai.server.data.migration.other.MockMigration

class AbstractMigrationImplementationRunnerTest {
  companion object {
    private const val HISTORY_COLLECTION_NAME = "history_collection"

    @JvmStatic
    fun migrationArgs(): Stream<MigrationArg> {
      val migrations = listOf(MockMigration(), MockMigration(), MockMigration())
      return Stream.of(
          MigrationArg(
              migrations = migrations,
              history = migrations.take(1).mapIndexed(migrationToHistoryRecord()),
              migrationCount = Result.success(2)))
    }
  }
  /*
   * 1) Successful new migrations
   * 2) No new migrations to perform
   * 3) Migration at index with invalid name
   * 4) Migration at index with invalid hash
   */

  @ParameterizedTest
  @MethodSource("migrationArgs")
  fun `performs migration`(arg: MigrationArg) {
    val registeredMigrations =
        arg.migrations.map { migration ->
          RegisteredMigration(migration = migration, helper = "Hello")
        }

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
    val runResult = runCatching { runner.run() }

    arg.migrations[0].didMigrate.shouldBe(false)
    arg.migrations[1].didMigrate.shouldBe(true)
    arg.migrations[2].didMigrate.shouldBe(true)

    val expectedQuery = Query().with(Sort.by(Sort.Direction.ASC, "index"))
    querySlot.captured.shouldBe(expectedQuery)

    val actualInsertedHistoryRecords = mutableListOf<MigrationHistoryRecord>()
    verify(exactly = 2) {
      mongoTemplate.insert(capture(actualInsertedHistoryRecords), HISTORY_COLLECTION_NAME)
    }

    val expectedInsertedHistoryRecords = migrations.drop(1).mapIndexed(migrationToHistoryRecord(1))
    actualInsertedHistoryRecords.shouldHaveSize(2).mapIndexed { index, actualRecord ->
      val expectedRecord = expectedInsertedHistoryRecords[index]
      expectedRecord.copy(timestamp = actualRecord.timestamp).let { actualRecord.shouldBe(it) }
    }
  }

  @Test
  fun `performs migration old`() {
    val migrations = listOf(MockMigration(), MockMigration(), MockMigration())

    val historyRecords = migrations.take(1).mapIndexed(migrationToHistoryRecord())
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

    val actualInsertedHistoryRecords = mutableListOf<MigrationHistoryRecord>()
    verify(exactly = 2) {
      mongoTemplate.insert(capture(actualInsertedHistoryRecords), HISTORY_COLLECTION_NAME)
    }

    val expectedInsertedHistoryRecords = migrations.drop(1).mapIndexed(migrationToHistoryRecord(1))
    actualInsertedHistoryRecords.shouldHaveSize(2).mapIndexed { index, actualRecord ->
      val expectedRecord = expectedInsertedHistoryRecords[index]
      expectedRecord.copy(timestamp = actualRecord.timestamp).let { actualRecord.shouldBe(it) }
    }
  }
}

private fun migrationToHistoryRecord(
    previousIndex: Int = 0
): (Int, Migration<*>) -> MigrationHistoryRecord = { index, migration ->
  MigrationHistoryRecord(
      index = index + previousIndex + 1,
      name = migration.javaClass.name,
      hash = generateMigrationHash(migration))
}

data class MigrationArg(
    val migrations: List<MockMigration>,
    val history: List<MigrationHistoryRecord>,
    val migrationCount: Result<Int>
)

class TestMigrationImplementationRunner(
    mongoTemplate: MongoTemplate,
    override val registeredMigrations: List<RegisteredMigration<*>>,
    override val collectionName: String
) : AbstractMigrationImplementationRunner(mongoTemplate)
