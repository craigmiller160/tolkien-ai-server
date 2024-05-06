package us.craigmiller160.tolkienai.server.data.repository

import java.time.ZonedDateTime
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import us.craigmiller160.tolkienai.server.data.entity.CHAT_LOG_COLLECTION
import us.craigmiller160.tolkienai.server.data.entity.ChatLog
import us.craigmiller160.tolkienai.server.data.log
import us.craigmiller160.tolkienai.server.web.type.ChatResponse

@Repository
class ChatLogRepository(private val mongoTemplate: MongoTemplate) {
  suspend fun insertChatResponse(chatDetails: ChatResponse): ChatLog =
      ChatLog(chatDetails).let { insertChatLog(it) }

  suspend fun insertChatLog(chatLog: ChatLog): ChatLog =
      withContext(Dispatchers.IO) { chatLog.copy(id = null).let { mongoTemplate.insert(chatLog) } }

  suspend fun insertAllChatLogs(chatLogs: List<ChatLog>): List<ChatLog> =
      withContext(Dispatchers.IO) {
        chatLogs.map { it.copy(id = null) }.let { mongoTemplate.insertAll(it) }.toList()
      }

  suspend fun insertAllChatResponses(chatDetails: List<ChatResponse>): List<ChatLog> =
      withContext(Dispatchers.IO) { chatDetails.map { ChatLog(it) }.let { insertAllChatLogs(it) } }

  suspend fun deleteAllChatLogs() =
      withContext(Dispatchers.IO) { mongoTemplate.remove(Query(), CHAT_LOG_COLLECTION) }

  suspend fun searchForChatLogs(
      page: Pageable,
      group: String? = null,
      startTimestamp: ZonedDateTime? = null,
      endTimestamp: ZonedDateTime? = null
  ): List<ChatLog> {
    val query =
        createSearchQuery(group, startTimestamp, endTimestamp)
            .with(page)
            .with(Sort.by(Sort.Order.desc("timestamp")))
            .also { it.log("Search For Chat Logs") }

    return mongoTemplate.find(query, ChatLog::class.java)
  }

  private fun createSearchQuery(
      group: String? = null,
      startTimestamp: ZonedDateTime? = null,
      endTimestamp: ZonedDateTime? = null
  ): Query =
      listOfNotNull(
              group?.let { Criteria.where("details.group").`is`(it) },
              startTimestamp?.let { Criteria.where("timestamp").gte(Date.from(it.toInstant())) },
              endTimestamp?.let { Criteria.where("timestamp").lte(Date.from(it.toInstant())) })
          .reduceOrNull { acc, criteria -> acc.andOperator(criteria) }
          ?.let { Query(it) }
          ?: Query()

  suspend fun getCountForSearchForChatLogs(
      group: String?,
      startTimestamp: ZonedDateTime? = null,
      endTimestamp: ZonedDateTime? = null
  ): Long {
    val query =
        createSearchQuery(group, startTimestamp, endTimestamp).also {
          it.log("Get Count For Search For Chat Logs")
        }
    return mongoTemplate.count(query, CHAT_LOG_COLLECTION)
  }
}
