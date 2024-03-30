package us.craigmiller160.tolkienai.server.data.migration.mongo

import com.mongodb.client.MongoClient
import java.nio.file.Paths
import java.security.MessageDigest
import kotlin.io.path.readBytes
import org.apache.commons.codec.binary.Hex
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findAll
import org.springframework.stereotype.Component
import us.craigmiller160.tolkienai.server.data.migration.Migration
import us.craigmiller160.tolkienai.server.data.migration.MigrationRecord
import us.craigmiller160.tolkienai.server.data.migration.mongo.migrations.V001_InitialSchema

@Component
class MongoMigrationRunner(
    private val client: MongoClient,
    private val mongoTemplate: MongoTemplate,
    @Value("\${spring.data.mongodb.database}") private val database: String
) {
  companion object {
    private const val MONGO_MIGRATION_HISTORY_COLLECTION = "mongo_migration_history"
  }

  private val db = client.getDatabase(database)

  private val registeredMigrations: List<RegisteredMigration<*>> =
      listOf(RegisteredMigration(index = 1, migration = V001_InitialSchema(), helper = db))

  fun run() {
    val records =
        mongoTemplate.findAll(MigrationRecord::class.java, MONGO_MIGRATION_HISTORY_COLLECTION)
    println(records)
  }

  private fun <T> runMigration(index: Int, migration: Migration<T>, helper: T) {
    val hash = generateHash(migration)
    //    migration.migrate(helper)
  }
}

private fun generateHash(migration: Migration<*>): String {
  val name = "${migration.javaClass.name.replace('.', '/')}.class"
  val uri = migration.javaClass.classLoader.getResource(name).toURI()
  val digest = MessageDigest.getInstance("SHA-256")
  return Paths.get(uri).readBytes().let { digest.digest(it) }.let { Hex.encodeHexString(it) }
}

data class RegisteredMigration<T>(val index: Int, val migration: Migration<T>, val helper: T)
