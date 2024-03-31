package us.craigmiller160.tolkienai.server.data.migration

import us.craigmiller160.tolkienai.server.data.migration.exception.MigrationException

data class MigrationName(val version: String, val name: String)

private val MIGRATION_NAME_REGEX = Regex("^V(?<version>.+)__(?<name>.+)\$")

fun getMigrationName(index: Int, migration: Migration<*>): MigrationName {
  val className = migration.javaClass.simpleName
  return MIGRATION_NAME_REGEX.matchEntire(className)?.let { regexResult ->
    MigrationName(
        version = regexResult.groups["version"]!!.value, name = regexResult.groups["name"]!!.value)
  }
      ?: throw MigrationException("Migration at index $index has invalid name: $className")
}
