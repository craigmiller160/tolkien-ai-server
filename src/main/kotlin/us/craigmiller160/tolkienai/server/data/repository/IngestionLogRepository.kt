package us.craigmiller160.tolkienai.server.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import us.craigmiller160.tolkienai.server.data.entity.INGESTION_LOG_COLLECTION
import us.craigmiller160.tolkienai.server.data.entity.IngestionDetails
import us.craigmiller160.tolkienai.server.data.entity.IngestionLog

@Repository
class IngestionLogRepository(private val mongoTemplate: MongoTemplate) {
  suspend fun insertIngestionLog(ingestionDetails: IngestionDetails) =
      withContext(Dispatchers.IO) {
        IngestionLog(ingestionDetails).let { mongoTemplate.insert(it) }
      }

  suspend fun deleteAllIngestionLogs() =
      withContext(Dispatchers.IO) { mongoTemplate.remove(Query(), INGESTION_LOG_COLLECTION) }
}
