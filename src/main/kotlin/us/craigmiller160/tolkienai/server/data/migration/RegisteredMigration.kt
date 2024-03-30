package us.craigmiller160.tolkienai.server.data.migration

data class RegisteredMigration<T>(val migration: Migration<T>, val helper: T)

fun <T> RegisteredMigration<T>.migrate() = migration.migrate(helper)

fun <T> RegisteredMigration<T>.generateHash(): String = generateMigrationHash(migration)
