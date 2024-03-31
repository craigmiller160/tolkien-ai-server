package us.craigmiller160.tolkienai.server.data.migration.mongo.migrations

import com.mongodb.client.model.Indexes
import us.craigmiller160.tolkienai.server.data.migration.mongo.MongoMigration
import us.craigmiller160.tolkienai.server.data.migration.mongo.MongoMigrationHelper

class V001_InitialSchema : MongoMigration {
  override fun migrate(helper: MongoMigrationHelper) {
    helper.database.createCollection("chatLog")
    val collection = helper.database.getCollection("chatLog")
    collection.createIndex(Indexes.ascending("chat.chatId"))
    collection.createIndex(Indexes.ascending("timestamp"))
  }
}
