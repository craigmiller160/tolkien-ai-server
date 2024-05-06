package us.craigmiller160.tolkienai.server.data.migration

import java.nio.file.Paths
import java.security.MessageDigest
import kotlin.io.path.readBytes
import org.apache.commons.codec.binary.Hex

fun generateMigrationHash(migration: Migration<*>): String {
  val name = "${migration.javaClass.name.replace('.', '/')}.class"
  val uri = migration.javaClass.classLoader.getResource(name).toURI()
  val digest = MessageDigest.getInstance("SHA-256")
  return Paths.get(uri).readBytes().let { digest.digest(it) }.let { Hex.encodeHexString(it) }
}
