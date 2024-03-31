package us.craigmiller160.tolkienai.server.data.migration

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import us.craigmiller160.tolkienai.server.data.migration.exception.MigrationException

abstract class AbstractMigrationImplementationRunner(private val mongoTemplate: MongoTemplate) :
    MigrationRunner {
  companion object {
    private val MIGRATION_NAME_REGEX = Regex("^V(?<version>.+)__(?<name>.+)\$")
  }

  private val log = LoggerFactory.getLogger(javaClass)

  abstract val registeredMigrations: List<RegisteredMigration<*>>

  abstract val collectionName: String
  override fun run() {
    log.debug("Finding and running migrations")
    val historyRecords =
        Query().with(Sort.by(Sort.Direction.ASC, "index")).let { query ->
          mongoTemplate.find(query, MigrationHistoryRecord::class.java, collectionName)
        }

    registeredMigrations.forEachIndexed { index, registeredMigration ->
      val historyRecord = getHistoryRecord(historyRecords, index)
      val actualIndex = index + 1
      if (historyRecord == null) {
        log.debug("Running MongoDB migration: ${registeredMigration.migration.javaClass.name}")
        registeredMigration.migrate()
        insertHistoryRecord(actualIndex, registeredMigration)
        return@forEachIndexed
      }

      if (historyRecord.name != registeredMigration.migration.javaClass.name) {
        throw MigrationException(
            "Migration at index $actualIndex has incorrect name. Expected: ${historyRecord.name} Actual: ${registeredMigration.migration.javaClass.name}")
      }

      if (historyRecord.hash != registeredMigration.generateHash()) {
        throw MigrationException(
            "Migration at index $actualIndex has invalid hash. Changes are not allowed after migration is applied.")
      }
    }
    log.debug("All migrations completed")
  }

  private fun getHistoryRecord(
      historyRecords: List<MigrationHistoryRecord>,
      index: Int
  ): MigrationHistoryRecord? {
    if (historyRecords.size > index) {
      return historyRecords[index]
    }
    return null
  }

  private fun insertHistoryRecord(index: Int, registeredMigration: RegisteredMigration<*>) =
      MigrationHistoryRecord(
              index = index,
              name = registeredMigration.migration.javaClass.name,
              hash = registeredMigration.generateHash())
          .let { mongoTemplate.insert(it, collectionName) }
}
