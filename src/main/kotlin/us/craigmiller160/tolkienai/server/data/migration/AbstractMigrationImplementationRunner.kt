package us.craigmiller160.tolkienai.server.data.migration

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import us.craigmiller160.tolkienai.server.data.migration.exception.MigrationException

abstract class AbstractMigrationImplementationRunner(private val mongoTemplate: MongoTemplate) :
    MigrationRunner {
  companion object {}

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
      val actualIndex = index + 1
      val migrationName = getMigrationName(actualIndex, registeredMigration.migration)
      val historyRecord = getHistoryRecord(historyRecords, index)
      if (historyRecord == null) {
        log.debug("Running MongoDB migration: ${registeredMigration.migration.javaClass.name}")
        registeredMigration.migrate()
        insertHistoryRecord(actualIndex, registeredMigration, migrationName)
        return@forEachIndexed
      }

      // TODO add test for this
      if (historyRecord.version != migrationName.version) {
        throw MigrationException(
            "Migration at index $actualIndex has incorrect version. Expected: ${historyRecord.version} Actual: ${migrationName.version}")
      }

      if (historyRecord.name != migrationName.name) {
        throw MigrationException(
            "Migration at index $actualIndex has incorrect name. Expected: ${historyRecord.name} Actual: ${migrationName.name}")
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

  private fun insertHistoryRecord(
      index: Int,
      registeredMigration: RegisteredMigration<*>,
      migrationName: MigrationName
  ) =
      MigrationHistoryRecord(
              index = index,
              version = migrationName.version,
              name = migrationName.name,
              hash = registeredMigration.generateHash())
          .let { mongoTemplate.insert(it, collectionName) }
}
