package us.craigmiller160.tolkienai.migration.mongo.migrations

import com.mongodb.client.model.Indexes
import us.craigmiller160.tolkienai.migration.mongo.MongoMigration
import us.craigmiller160.tolkienai.migration.mongo.MongoMigrationHelper

class V20240404a__ChatGroup : MongoMigration {
  override fun migrate(helper: MongoMigrationHelper) {
    helper.database.getCollection("chatLog").also { collection ->
      collection.createIndex(Indexes.ascending("details.group"))
    }
  }
}
