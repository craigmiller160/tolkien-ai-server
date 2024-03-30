package us.craigmiller160.tolkienai.server.data.migration

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import us.craigmiller160.tolkienai.server.data.migration.exception.MigrationException
import us.craigmiller160.tolkienai.server.data.migration.other.MockMigration

class AbstractMigrationImplementationRunnerTest {
  companion object {
    private const val HISTORY_COLLECTION_NAME = "history_collection"

    @JvmStatic
    fun migrationArgs(): Stream<MigrationArg> {
      return Stream.of(
          MigrationArg(
              migrations = listOf(MockMigration(), MockMigration(), MockMigration()),
              historyCreator = { migrations ->
                migrations.take(1).mapIndexed(migrationToHistoryRecord())
              },
              migrationCount = Result.success(2)),
          MigrationArg(
              migrations = listOf(MockMigration(), MockMigration(), MockMigration()),
              historyCreator = { migrations -> migrations.mapIndexed(migrationToHistoryRecord()) },
              migrationCount = Result.success(0)),
          MigrationArg(
              migrations = listOf(MockMigration(), MockMigration(), MockMigration()),
              historyCreator = { migrations ->
                migrations.mapIndexed(migrationToHistoryRecord()).mapIndexed { index, record ->
                  if (index == 1) {
                    return@mapIndexed record.copy(name = "abc")
                  }
                  return@mapIndexed record
                }
              },
              migrationCount =
                  Result.failure(
                      MigrationException(
                          "Migration at index 2 has incorrect name. Expected: abc Actual: ${MockMigration::class.java.name}"))),
          MigrationArg(
              migrations = listOf(MockMigration(), MockMigration(), MockMigration()),
              historyCreator = { migrations ->
                migrations.mapIndexed(migrationToHistoryRecord()).mapIndexed { index, record ->
                  if (index == 1) {
                    return@mapIndexed record.copy(hash = "abc")
                  }
                  return@mapIndexed record
                }
              },
              migrationCount =
                  Result.failure(
                      MigrationException(
                          "Migration at index 2 has invalid hash. Changes are not allowed after migration is applied."))))
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
    } returns arg.history

    every {
      mongoTemplate.insert(any(MigrationHistoryRecord::class), HISTORY_COLLECTION_NAME)
    } answers { arg(0) }

    val runner =
        TestMigrationImplementationRunner(
            mongoTemplate, registeredMigrations, HISTORY_COLLECTION_NAME)
    val runResult = runCatching { runner.run() }
    if (arg.migrationCount.isFailure) {
      runResult.shouldBeFailure(arg.migrationCount.exceptionOrNull()!!)
      return
    }

    val migrationCount = arg.migrationCount.getOrThrow()

    arg.migrations.take(arg.migrations.size - migrationCount).forEach { migration ->
      migration.didMigrate.shouldBe(false)
    }

    arg.migrations.drop(arg.migrations.size - migrationCount).forEach { migration ->
      migration.didMigrate.shouldBe(true)
    }

    val expectedQuery = Query().with(Sort.by(Sort.Direction.ASC, "index"))
    querySlot.captured.shouldBe(expectedQuery)

    val actualInsertedHistoryRecords = mutableListOf<MigrationHistoryRecord>()
    verify(exactly = migrationCount) {
      mongoTemplate.insert(capture(actualInsertedHistoryRecords), HISTORY_COLLECTION_NAME)
    }

    val expectedInsertedHistoryRecords =
        arg.migrations
            .drop(arg.migrations.size - migrationCount)
            .mapIndexed(migrationToHistoryRecord(arg.migrations.size - migrationCount))
    actualInsertedHistoryRecords.shouldHaveSize(migrationCount).forEachIndexed { index, actualRecord
      ->
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
    val historyCreator: (List<MockMigration>) -> List<MigrationHistoryRecord>,
    val migrationCount: Result<Int>
) {
  val history: List<MigrationHistoryRecord> = historyCreator(migrations)
}

class TestMigrationImplementationRunner(
    mongoTemplate: MongoTemplate,
    override val registeredMigrations: List<RegisteredMigration<*>>,
    override val collectionName: String
) : AbstractMigrationImplementationRunner(mongoTemplate)
