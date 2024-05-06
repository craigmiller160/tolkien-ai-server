package us.craigmiller160.tolkienai.migration

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import us.craigmiller160.tolkienai.migration.config.MigrationImplementationProperties
import us.craigmiller160.tolkienai.migration.exception.MigrationException

abstract class AbstractMigrationImplementationRunner<Helper>(
    private val mongoTemplate: MongoTemplate,
    private val properties: MigrationImplementationProperties
) : MigrationRunner {
  companion object {}

  private val log = LoggerFactory.getLogger(javaClass)

  abstract val collectionName: String

  abstract val helper: Helper

  override fun run(): List<MigrationReport> {
    if (!properties.enabled) {
      log.info("{} disabled", javaClass.simpleName)
      return listOf()
    }

    log.debug("Finding and running migrations")
    val historyRecords =
        Query().with(Sort.by(Sort.Direction.ASC, "index")).let { query ->
          mongoTemplate.find(query, MigrationHistoryRecord::class.java, collectionName)
        }

    return loadMigrations<Helper>(*properties.migrationPaths.toTypedArray())
        .mapIndexed { index, migration ->
          val actualIndex = index + 1
          val migrationName =
              us.craigmiller160.tolkienai.migration.getMigrationName(actualIndex, migration)
          val historyRecord = getHistoryRecord(historyRecords, index)
          if (historyRecord == null) {
            log.debug("Running migration: ${migration.javaClass.name}")
            migration.migrate(helper)
            insertHistoryRecord(actualIndex, migration, migrationName)
            return@mapIndexed us.craigmiller160.tolkienai.migration.MigrationReport(
                migrationName = migration.javaClass.simpleName, executed = true)
          }

          if (historyRecord.version != migrationName.version) {
            throw MigrationException(
                "Migration at index $actualIndex has incorrect version. Expected: ${historyRecord.version} Actual: ${migrationName.version}")
          }

          if (historyRecord.name != migrationName.name) {
            throw MigrationException(
                "Migration at index $actualIndex has incorrect name. Expected: ${historyRecord.name} Actual: ${migrationName.name}")
          }

          if (historyRecord.hash !=
              us.craigmiller160.tolkienai.migration.generateMigrationHash(migration)) {
            throw MigrationException(
                "Migration at index $actualIndex has invalid hash. Changes are not allowed after migration is applied.")
          }

          return@mapIndexed us.craigmiller160.tolkienai.migration.MigrationReport(
              migrationName = migration.javaClass.simpleName, executed = false)
        }
        .also { log.debug("All migrations completed") }
  }

  private fun getHistoryRecord(
      historyRecords: List<us.craigmiller160.tolkienai.migration.MigrationHistoryRecord>,
      index: Int
  ): us.craigmiller160.tolkienai.migration.MigrationHistoryRecord? {
    if (historyRecords.size > index) {
      return historyRecords[index]
    }
    return null
  }

  private fun insertHistoryRecord(
      index: Int,
      migration: us.craigmiller160.tolkienai.migration.Migration<Helper>,
      migrationName: us.craigmiller160.tolkienai.migration.MigrationName
  ) =
      us.craigmiller160.tolkienai.migration
          .MigrationHistoryRecord(
              index = index,
              version = migrationName.version,
              name = migrationName.name,
              hash = us.craigmiller160.tolkienai.migration.generateMigrationHash(migration))
          .let { mongoTemplate.insert(it, collectionName) }
}
