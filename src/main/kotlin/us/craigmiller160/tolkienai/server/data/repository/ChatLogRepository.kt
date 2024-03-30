package us.craigmiller160.tolkienai.server.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository
import us.craigmiller160.tolkienai.server.data.entity.ChatLog
import us.craigmiller160.tolkienai.server.web.type.ChatResponse

@Repository
class ChatLogRepository(private val mongoTemplate: MongoTemplate) {
  suspend fun insertChatLog(chat: ChatResponse) =
      withContext(Dispatchers.IO) { ChatLog(chat).let { mongoTemplate.insert(it) } }
}
