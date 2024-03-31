package us.craigmiller160.tolkienai.server.data.migration

import us.craigmiller160.tolkienai.server.data.migration.exception.MigrationException

data class MigrationName(val version: String, val name: String)

private val MIGRATION_NAME_REGEX = Regex("^V(?<version>.+)__(?<name>.+)\$")

fun getMigrationName(index: Int, migration: Migration<*>): MigrationName {
  if (!MIGRATION_NAME_REGEX.matches(migration.javaClass.simpleName)) {
    throw MigrationException(
        "Migration at index $index has invalid name: ${migration.javaClass.simpleName}")
  }
}
