package us.craigmiller160.tolkienai.migration.mongo.migrations

import com.mongodb.client.model.Indexes
import us.craigmiller160.tolkienai.migration.mongo.MongoMigration
import us.craigmiller160.tolkienai.migration.mongo.MongoMigrationHelper

class V20240330__InitialSchema : MongoMigration {
  override fun migrate(helper: MongoMigrationHelper) {
    helper.database.createCollection("chatLog")
    val collection = helper.database.getCollection("chatLog")
    collection.createIndex(Indexes.ascending("chat.chatId"))
    collection.createIndex(Indexes.ascending("timestamp"))
  }
}
