package us.craigmiller160.tolkienai.server.migration.mongo.migrations

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Indexes
import us.craigmiller160.tolkienai.server.data.migration.mongo.MongoMigration
import us.craigmiller160.tolkienai.server.data.migration.mongo.MongoMigrationHelper

class V20240404__UpdatingLogs : MongoMigration {
  override fun migrate(helper: MongoMigrationHelper) {
    helper.database.getCollection("chatLog").also { collection ->
      collection.deleteMany(Filters.empty())
      collection.dropIndex(Indexes.ascending("chat.chatId"))
      collection.createIndex(Indexes.ascending("details.chatId"))
    }

    helper.database.createCollection("ingestionLog")
  }
}
