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
  suspend fun insertIngestionLog(ingestionDetails: IngestionDetails): IngestionLog =
      IngestionLog(ingestionDetails).let { insertIngestionLog(it) }

  suspend fun insertIngestionLog(ingestionLog: IngestionLog): IngestionLog =
      withContext(Dispatchers.IO) { ingestionLog.copy(id = null).let { mongoTemplate.insert(it) } }

  suspend fun insertAllIngestionLogs(ingestionLogs: List<IngestionLog>): List<IngestionLog> =
      withContext(Dispatchers.IO) {
        ingestionLogs.map { it.copy(id = null) }.let { mongoTemplate.insertAll(it) }.toList()
      }

  suspend fun insertAllIngestionLogs(ingestionDetails: List<IngestionDetails>): List<IngestionLog> =
      ingestionDetails.map { IngestionLog(it) }.let { insertAllIngestionLogs(it) }

  suspend fun deleteAllIngestionLogs() =
      withContext(Dispatchers.IO) { mongoTemplate.remove(Query(), INGESTION_LOG_COLLECTION) }
}
