package us.craigmiller160.tolkienai.server.data.migration

import io.kotest.matchers.shouldBe
import java.nio.file.Paths
import java.security.MessageDigest
import kotlin.io.path.readBytes
import org.apache.commons.codec.binary.Hex
import org.junit.jupiter.api.Test
import us.craigmiller160.tolkienai.server.data.migration.other.DummyMigration

class MigrationHashTest {
  @Test
  fun `generateMigrationHash generates correct hash`() {
    val digest = MessageDigest.getInstance("SHA-256")
    val expected =
        Paths.get(
                System.getProperty("user.dir"),
                "build/classes/kotlin/test/us/craigmiller160/tolkienai/server/data/migration/other/DummyMigration.class")
            .readBytes()
            .let { digest.digest(it) }
            .let { Hex.encodeHexString(it) }

    val migration = DummyMigration()
    val actual = generateMigrationHash(migration)
    actual.shouldBe(expected)
  }
}
