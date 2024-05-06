package us.craigmiller160.tolkienai.server.migration

import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Test
import us.craigmiller160.tolkienai.server.migration.test_migrations.V20240330__InitialMigration
import us.craigmiller160.tolkienai.server.migration.test_migrations.V20240331__MigrationTwo
import us.craigmiller160.tolkienai.server.migration.test_migrations.V20240401__MigrationThree
import us.craigmiller160.tolkienai.server.migration.test_migrations.bad.BadMockMigration

class MigrationLoaderTest {
  companion object {
    private const val DEFAULT_MIGRATION_LOCATION =
        "classpath:us/craigmiller160/tolkienai/server/data/migration/test_migrations"
    private const val BAD_MIGRATION_LOCATION = "$DEFAULT_MIGRATION_LOCATION/bad"
  }

  @Test
  fun `loads migrations`() {
    val migrations = loadMigrations<String>(DEFAULT_MIGRATION_LOCATION, BAD_MIGRATION_LOCATION)
    migrations
        .shouldHaveSize(4)
        .map { it.javaClass.simpleName }
        .shouldContainInOrder(
            BadMockMigration::class.java.simpleName,
            V20240330__InitialMigration::class.java.simpleName,
            V20240331__MigrationTwo::class.java.simpleName,
            V20240401__MigrationThree::class.java.simpleName)
  }
}
