import com.mongodb.client.model.Indexes
import us.craigmiller160.tolkienai.migration.mongo.MongoMigration
import us.craigmiller160.tolkienai.migration.mongo.MongoMigrationHelper

class V20240505__IngestionLogIndexes : MongoMigration {
  override fun migrate(helper: MongoMigrationHelper) {
    helper.database.getCollection("ingestionLog").also { collection ->
      collection.createIndex(Indexes.ascending("timestamp"))
    }
  }
}
