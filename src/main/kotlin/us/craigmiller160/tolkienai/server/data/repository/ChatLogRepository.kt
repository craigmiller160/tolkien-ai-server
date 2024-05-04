package us.craigmiller160.tolkienai.server.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import us.craigmiller160.tolkienai.server.data.entity.CHAT_LOG_COLLECTION
import us.craigmiller160.tolkienai.server.data.entity.ChatLog
import us.craigmiller160.tolkienai.server.web.type.ChatResponse

@Repository
class ChatLogRepository(private val mongoTemplate: MongoTemplate) {
  suspend fun insertChatLog(chatDetails: ChatResponse): ChatLog =
      ChatLog(chatDetails).let { insertChatLog(it) }

  suspend fun insertChatLog(chatLog: ChatLog): ChatLog =
      withContext(Dispatchers.IO) { chatLog.copy(id = null).let { mongoTemplate.insert(chatLog) } }

  suspend fun insertAllChatLogs(chatLogs: List<ChatLog>): List<ChatLog> =
      withContext(Dispatchers.IO) {
        chatLogs.map { it.copy(id = null) }.let { mongoTemplate.insertAll(it) }.toList()
      }

  suspend fun insertAllChatLogs(chatDetails: List<ChatResponse>): List<ChatLog> =
      withContext(Dispatchers.IO) { chatDetails.map { ChatLog(it) }.let { insertAllChatLogs(it) } }

  suspend fun deleteAllChatLogs() =
      withContext(Dispatchers.IO) { mongoTemplate.remove(Query(), CHAT_LOG_COLLECTION) }
}
