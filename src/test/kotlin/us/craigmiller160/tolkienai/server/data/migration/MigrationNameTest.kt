package us.craigmiller160.tolkienai.server.data.migration

import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import us.craigmiller160.tolkienai.server.data.migration.exception.MigrationException
import us.craigmiller160.tolkienai.server.data.migration.other.BadMockMigration
import us.craigmiller160.tolkienai.server.data.migration.test_migrations.V20240330__InitialMigration

class MigrationNameTest {
  companion object {
    @JvmStatic
    fun migrationNameArgs(): Stream<MigrationNameArg> {
      return Stream.of(
          MigrationNameArg(
              migration = V20240330__InitialMigration(),
              expectedResult =
                  Result.success(MigrationName(version = "20240330", name = "InitialMigration"))),
          MigrationNameArg(
              migration = BadMockMigration(),
              expectedResult =
                  Result.failure(
                      MigrationException(
                          "Migration at index 1 has invalid name: ${BadMockMigration::class.java.simpleName}"))))
    }
  }

  @ParameterizedTest
  @MethodSource("migrationNameArgs")
  fun `validates and extracts migration name`(arg: MigrationNameArg) {
    val actualResult = kotlin.runCatching { getMigrationName(1, arg.migration) }
    if (actualResult.isSuccess) {
      actualResult.shouldBeSuccess(arg.expectedResult.getOrThrow())
    } else {
      actualResult
          .shouldBeFailure<MigrationException>()
          .message
          .shouldBe(arg.expectedResult.exceptionOrNull()!!.message)
    }
  }
}

data class MigrationNameArg(val migration: Migration<*>, val expectedResult: Result<MigrationName>)
