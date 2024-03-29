package us.craigmiller160.tolkienai.server.web.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import us.craigmiller160.tolkienai.server.ai.dto.floatEmbedding
import us.craigmiller160.tolkienai.server.ai.service.OpenAiService
import us.craigmiller160.tolkienai.server.ai.service.WeaviateService
import us.craigmiller160.tolkienai.server.web.type.ChatRequest
import us.craigmiller160.tolkienai.server.web.type.ChatResponse

@Service
class ChatService(
    private val weaviateService: WeaviateService,
    private val openAiService: OpenAiService
) {

  private val log = LoggerFactory.getLogger(javaClass)
  suspend fun chat(request: ChatRequest): ChatResponse {
    log.info("Preparing chat for query: ${request.query}")
    openAiService.createEmbedding(request.query).let { queryEmbedding ->
      weaviateService.searchForEmbeddings(queryEmbedding.floatEmbedding, 10)
    }
    TODO()
  }
}
