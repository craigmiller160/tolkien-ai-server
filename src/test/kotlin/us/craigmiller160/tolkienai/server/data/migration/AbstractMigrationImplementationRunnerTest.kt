package us.craigmiller160.tolkienai.server.data.migration

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
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
import us.craigmiller160.tolkienai.server.config.MigrationImplementationProperties
import us.craigmiller160.tolkienai.server.data.migration.exception.MigrationException
import us.craigmiller160.tolkienai.server.data.migration.other.AbstractMockMigration
import us.craigmiller160.tolkienai.server.data.migration.other.BadMockMigration
import us.craigmiller160.tolkienai.server.data.migration.test_migrations.V20240330__InitialMigration
import us.craigmiller160.tolkienai.server.data.migration.test_migrations.V20240331__MigrationTwo
import us.craigmiller160.tolkienai.server.data.migration.test_migrations.V20240401__MigrationThree

class AbstractMigrationImplementationRunnerTest {
  companion object {
    private const val HISTORY_COLLECTION_NAME = "history_collection"
    private const val DEFAULT_MIGRATION_LOCATION =
        "classpath:us/craigmiller160/tolkienai/server/data/migration/test_migrations"

    @JvmStatic
    fun migrationArgs(): Stream<MigrationArg> {
      return Stream.of(
          MigrationArg(
              migrations = defaultMigrationList(),
              historyCreator = newHistoryCreator { it.take(1) },
              migrationCount = Result.success(2)),
          MigrationArg(
              migrations = defaultMigrationList(),
              historyCreator = newHistoryCreator(),
              migrationCount = Result.success(0)),
          MigrationArg(
              migrations = defaultMigrationList(),
              historyCreator =
                  newHistoryCreator { history ->
                    history.mapIndexed { index, record ->
                      if (index == 1) {
                        return@mapIndexed record.copy(version = "abc")
                      }
                      return@mapIndexed record
                    }
                  },
              migrationCount =
                  Result.failure(
                      MigrationException(
                          "Migration at index 2 has incorrect version. Expected: abc Actual: 20240331"))),
          MigrationArg(
              migrations = defaultMigrationList(),
              historyCreator =
                  newHistoryCreator { history ->
                    history.mapIndexed { index, record ->
                      if (index == 1) {
                        return@mapIndexed record.copy(name = "abc")
                      }
                      return@mapIndexed record
                    }
                  },
              migrationCount =
                  Result.failure(
                      MigrationException(
                          "Migration at index 2 has incorrect name. Expected: abc Actual: MigrationTwo"))),
          MigrationArg(
              migrations = defaultMigrationList(),
              historyCreator =
                  newHistoryCreator { history ->
                    history.mapIndexed { index, record ->
                      if (index == 1) {
                        return@mapIndexed record.copy(hash = "abc")
                      }
                      return@mapIndexed record
                    }
                  },
              migrationCount =
                  Result.failure(
                      MigrationException(
                          "Migration at index 2 has invalid hash. Changes are not allowed after migration is applied."))),
          MigrationArg(
              migrations = defaultMigrationList().let { it.take(2) + listOf(BadMockMigration()) },
              historyCreator = newHistoryCreator(),
              migrationCount =
                  Result.failure(
                      MigrationException(
                          "Migration at index 3 has invalid name: ${BadMockMigration::class.java.simpleName}"))))
    }
  }

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
      runResult
          .shouldBeFailure<MigrationException>()
          .message
          .shouldBe(arg.migrationCount.exceptionOrNull()!!.message)
      return
    }

    val migrationCount = arg.migrationCount.getOrThrow()
    runResult.shouldBeSuccess()

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

typealias HistoryCreator = (List<AbstractMockMigration>) -> List<MigrationHistoryRecord>

private fun migrationToHistoryRecord(
    previousIndex: Int = 0
): (Int, Migration<*>) -> MigrationHistoryRecord = { index, migration ->
  val migrationName =
      runCatching { getMigrationName(index, migration) }.getOrElse { MigrationName("", "") }
  MigrationHistoryRecord(
      index = index + previousIndex + 1,
      name = migrationName.name,
      version = migrationName.version,
      hash = generateMigrationHash(migration))
}

private fun defaultMigrationList(): List<AbstractMockMigration> =
    listOf(V20240330__InitialMigration(), V20240331__MigrationTwo(), V20240401__MigrationThree())

private fun newHistoryCreator(
    modifier: (List<MigrationHistoryRecord>) -> (List<MigrationHistoryRecord>) = { it }
): HistoryCreator = { migrations ->
  migrations.mapIndexed(migrationToHistoryRecord()).let(modifier)
}

data class MigrationArg(
    val migrations: List<AbstractMockMigration>,
    val historyCreator: HistoryCreator,
    val migrationCount: Result<Int>
) {
  val history: List<MigrationHistoryRecord> = historyCreator(migrations)
}

class TestMigrationImplementationRunner(
    mongoTemplate: MongoTemplate,
    paths: List<String>,
    override val registeredMigrations: List<RegisteredMigration<*>>,
    override val collectionName: String
) : AbstractMigrationImplementationRunner(mongoTemplate, MigrationImplementationProperties(paths))
