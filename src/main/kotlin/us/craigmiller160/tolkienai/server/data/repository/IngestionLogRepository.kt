package us.craigmiller160.tolkienai.server.data.repository

import java.time.ZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.count
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import us.craigmiller160.tolkienai.server.data.entity.INGESTION_LOG_COLLECTION
import us.craigmiller160.tolkienai.server.data.entity.IngestionDetails
import us.craigmiller160.tolkienai.server.data.entity.IngestionLog

@Repository
class IngestionLogRepository(private val mongoTemplate: MongoTemplate) {
  suspend fun insertIngestionDetails(ingestionDetails: IngestionDetails): IngestionLog =
      IngestionLog(ingestionDetails).let { insertIngestionLog(it) }

  suspend fun insertIngestionLog(ingestionLog: IngestionLog): IngestionLog =
      withContext(Dispatchers.IO) { ingestionLog.copy(id = null).let { mongoTemplate.insert(it) } }

  suspend fun insertAllIngestionLogs(ingestionLogs: List<IngestionLog>): List<IngestionLog> =
      withContext(Dispatchers.IO) {
        ingestionLogs.map { it.copy(id = null) }.let { mongoTemplate.insertAll(it) }.toList()
      }

  suspend fun insertAllIngestionDetails(
      ingestionDetails: List<IngestionDetails>
  ): List<IngestionLog> =
      ingestionDetails.map { IngestionLog(it) }.let { insertAllIngestionLogs(it) }

  suspend fun deleteAllIngestionLogs() =
      withContext(Dispatchers.IO) { mongoTemplate.remove(Query(), INGESTION_LOG_COLLECTION) }

  suspend fun searchForIngestionLogs(
      pageNumber: Int,
      pageSize: Int,
      startTimestamp: ZonedDateTime? = null,
      endTimestamp: ZonedDateTime? = null
  ): List<IngestionLog> {
    val query = createSearchQuery(startTimestamp, endTimestamp)

    return mongoTemplate.find(query, IngestionLog::class.java)
  }

  private fun createSearchQuery(
      startTimestamp: ZonedDateTime? = null,
      endTimestamp: ZonedDateTime? = null
  ): Query =
      listOfNotNull(
              startTimestamp?.let { Criteria.where("timestamp").gte(it) },
              endTimestamp?.let { Criteria.where("timestamp").lte(it) })
          .reduceOrNull { acc, criteria -> acc.andOperator(criteria) }
          ?.let { Query(it) }
          ?: Query()

  suspend fun getCountForSearchForIngestionLogs(
      group: String? = null,
      startTimestamp: ZonedDateTime? = null,
      endTimestamp: ZonedDateTime? = null
  ): Long {
    val query = createSearchQuery(startTimestamp, endTimestamp)
    return mongoTemplate.count(query, INGESTION_LOG_COLLECTION)
  }
}
