package us.craigmiller160.tolkienai.server.web.service

import org.springframework.stereotype.Service
import us.craigmiller160.tolkienai.server.ai.service.WeaviateService
import us.craigmiller160.tolkienai.server.web.type.ChatRequest
import us.craigmiller160.tolkienai.server.web.type.ChatResponse

@Service
class ChatService(private val weaviateService: WeaviateService) {
  suspend fun chat(request: ChatRequest): ChatResponse {
    TODO()
  }
}
