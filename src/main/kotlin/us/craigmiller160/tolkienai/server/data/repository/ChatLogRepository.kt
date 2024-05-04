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
  suspend fun insertChatLog(chat: ChatResponse) =
      withContext(Dispatchers.IO) { ChatLog(chat).let { mongoTemplate.insert(it) } }

  suspend fun deleteAllChatLogs() =
      withContext(Dispatchers.IO) { mongoTemplate.remove(Query(), CHAT_LOG_COLLECTION) }
}
