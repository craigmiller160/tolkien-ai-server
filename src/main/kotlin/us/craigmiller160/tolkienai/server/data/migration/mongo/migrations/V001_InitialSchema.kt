package us.craigmiller160.tolkienai.server.data.migration.mongo.migrations

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Indexes
import us.craigmiller160.tolkienai.server.data.migration.mongo.MongoCoreMigration

class V001_InitialSchema : MongoCoreMigration {
  override fun migrate(helper: MongoDatabase) {
    helper.createCollection("chatLog")
    val collection = helper.getCollection("chatLog")
    collection.createIndex(Indexes.ascending("chat.chatId"))
  }
}
