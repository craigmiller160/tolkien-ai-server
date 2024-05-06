package us.craigmiller160.tolkienai.server.data.migration.other

import us.craigmiller160.tolkienai.server.data.migration.Migration

abstract class AbstractMockMigration : Migration<String> {
  var didMigrate: Boolean = false
  override fun migrate(helper: String) {
    didMigrate = true
  }
}
