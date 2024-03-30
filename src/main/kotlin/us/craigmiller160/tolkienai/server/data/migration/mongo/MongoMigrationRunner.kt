package us.craigmiller160.tolkienai.server.data.migration.mongo

import com.mongodb.client.MongoClient
import java.nio.file.Paths
import java.security.MessageDigest
import kotlin.io.path.readBytes
import org.apache.commons.codec.binary.Hex
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import us.craigmiller160.tolkienai.server.data.migration.Migration
import us.craigmiller160.tolkienai.server.data.migration.mongo.migrations.V001_InitialSchema

@Component
class MongoMigrationRunner(
    private val client: MongoClient,
    private val mongoTemplate: MongoTemplate,
    @Value("\${spring.data.mongodb.database}") private val database: String
) {
  fun run() {
    val db = client.getDatabase(database)
    runMigration(1, V001_InitialSchema(), db)
  }

  private fun <T> runMigration(index: Int, migration: Migration<T>, helper: T) {
    val name = "${migration.javaClass.name.replace('.', '/')}.class"
    val uri = javaClass.classLoader.getResource(name).toURI()
    println(Paths.get(uri))
    //    migration.migrate(helper)
  }
}

private fun generateHash(migration: Migration<T>): String {
  val name = "${migration.javaClass.name.replace('.', '/')}.class"
  val uri = migration.javaClass.classLoader.getResource(name).toURI()
  val digest = MessageDigest.getInstance("SHA-256")
  return Paths.get(uri).readBytes().let { digest.digest(it) }.let { Hex.encodeHexString(it) }
}
