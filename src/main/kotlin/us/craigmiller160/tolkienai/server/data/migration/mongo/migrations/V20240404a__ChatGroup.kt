package us.craigmiller160.tolkienai.server.data.migration.mongo.migrations

import com.mongodb.client.model.Indexes
import us.craigmiller160.tolkienai.server.data.migration.mongo.MongoMigration
import us.craigmiller160.tolkienai.server.data.migration.mongo.MongoMigrationHelper

class V20240404a__ChatGroup : MongoMigration {
  override fun migrate(helper: MongoMigrationHelper) {
    helper.database.getCollection("chatLog").also { collection ->
      collection.createIndex(Indexes.ascending("group"))
    }
  }
}
