package us.craigmiller160.tolkienai.server.data.migration.other

import us.craigmiller160.tolkienai.server.data.migration.Migration

class DummyMigration : Migration<String> {
  override fun migrate(helper: String) {
    println(helper)
  }
}
