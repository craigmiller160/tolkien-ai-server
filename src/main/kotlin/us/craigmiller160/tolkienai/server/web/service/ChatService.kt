package us.craigmiller160.tolkienai.server.web.service

import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import us.craigmiller160.tolkienai.server.ai.dto.ChatMessageRole
import us.craigmiller160.tolkienai.server.ai.dto.floatEmbedding
import us.craigmiller160.tolkienai.server.ai.service.OpenAiService
import us.craigmiller160.tolkienai.server.ai.service.WeaviateService
import us.craigmiller160.tolkienai.server.config.ChatProperties
import us.craigmiller160.tolkienai.server.web.type.ChatExplanation
import us.craigmiller160.tolkienai.server.web.type.ChatRequest
import us.craigmiller160.tolkienai.server.web.type.ChatResponse

@Service
class ChatService(
    private val weaviateService: WeaviateService,
    private val openAiService: OpenAiService,
    private val chatProperties: ChatProperties
) {

  private val log = LoggerFactory.getLogger(javaClass)
  fun chat(request: ChatRequest): ChatResponse {
    val id = UUID.randomUUID()
    log.info("Preparing chat. Chat ID: $id Query: ${request.query}")
    return runBlocking {
      val textMatches =
          openAiService
              .createEmbedding(request.query)
              .let { queryEmbedding ->
                weaviateService.searchForEmbeddings(
                    queryEmbedding.floatEmbedding, chatProperties.query.recordLimit)
              }
              .joinToString("\n") { it.text }

      openAiService.createChat(
          ChatMessageRole.USER to request.query,
          ChatMessageRole.SYSTEM to
              "You are an expert on the works of JRR Tolkien and passionate about educating others on it. You will answer relying only on the provided list of data.",
          ChatMessageRole.USER to "List of data: \n$textMatches")

      ChatResponse(
          chatId = id,
          response = "",
          explanation =
              ChatExplanation(query = request.query, embeddingMatches = results.map { it.text }))
    }
  }
}
