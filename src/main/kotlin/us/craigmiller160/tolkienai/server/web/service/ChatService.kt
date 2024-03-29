package us.craigmiller160.tolkienai.server.web.service

import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import us.craigmiller160.tolkienai.server.ai.dto.ChatMessageContainer
import us.craigmiller160.tolkienai.server.ai.dto.ChatMessageRole
import us.craigmiller160.tolkienai.server.ai.dto.floatEmbedding
import us.craigmiller160.tolkienai.server.ai.service.OpenAiService
import us.craigmiller160.tolkienai.server.ai.service.WeaviateService
import us.craigmiller160.tolkienai.server.config.ChatProperties
import us.craigmiller160.tolkienai.server.web.data.repository.ChatLogRepository
import us.craigmiller160.tolkienai.server.web.type.ChatExplanation
import us.craigmiller160.tolkienai.server.web.type.ChatRequest
import us.craigmiller160.tolkienai.server.web.type.ChatResponse

@Service
class ChatService(
    private val weaviateService: WeaviateService,
    private val openAiService: OpenAiService,
    private val chatProperties: ChatProperties,
    private val chatLogRepository: ChatLogRepository
) {
  companion object {
    private const val SYSTEM_MESSAGE =
        "You are an expert on the works of JRR Tolkien and passionate about educating others on it. You will answer relying only on the provided list of data."
  }

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
              .map { it.text }
      val textMatchesString = textMatches.joinToString("\n")

      val baseMessages =
          listOf(
              ChatMessageContainer(ChatMessageRole.USER, request.query),
              ChatMessageContainer(ChatMessageRole.SYSTEM, SYSTEM_MESSAGE))

      val chatResult =
          openAiService.createChat(
              baseMessages +
                  listOf(
                      ChatMessageContainer(
                          ChatMessageRole.USER, "List of data: \n$textMatchesString")))

      ChatResponse(
              chatId = id,
              response = chatResult.response,
              explanation = ChatExplanation(query = baseMessages, embeddingMatches = textMatches))
          .also { chatLogRepository.insertChatLog(it) }
    }
  }
}
