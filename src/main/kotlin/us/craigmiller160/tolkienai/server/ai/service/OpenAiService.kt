package us.craigmiller160.tolkienai.server.ai.service

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import us.craigmiller160.tolkienai.server.ai.dto.ChatContainer
import us.craigmiller160.tolkienai.server.ai.dto.ChatMessageContainer
import us.craigmiller160.tolkienai.server.ai.dto.ChatMessageRole
import us.craigmiller160.tolkienai.server.ai.dto.EmbeddingContainer
import us.craigmiller160.tolkienai.server.config.OpenaiProperties

@Service
class OpenAiService(
    private val openAiClient: OpenAI,
    private val openaiProperties: OpenaiProperties
) {
  private val log = LoggerFactory.getLogger(javaClass)
  suspend fun createEmbedding(text: String): EmbeddingContainer =
      ModelId(openaiProperties.models.embedding.name)
          .let { EmbeddingRequest(model = it, input = listOf(text)) }
          .let { openAiClient.embeddings(it) }
          .also { res ->
            val promptTokens = res.usage.promptTokens ?: 0
            val totalTokens = res.usage.totalTokens ?: 0
            log.trace("Embedding token usage. Prompt: $promptTokens Total: $totalTokens")
          }
          .let { res -> res.embeddings.flatMap { it.embedding } }
          .let {
            EmbeddingContainer(
                embedding = it,
                text = text,
                dimensions = openaiProperties.models.embedding.dimensions)
          }

  suspend fun createChat(messages: List<ChatMessageContainer>): ChatContainer =
      ChatCompletionRequest(
              model = ModelId(openaiProperties.models.chat.name),
              messages =
                  messages.map { (role, content) ->
                    ChatMessage(role = role.toChatRole(), content = content)
                  })
          .let { openAiClient.chatCompletion(it) }
          .also { res ->
            val promptTokens = res.usage?.promptTokens ?: 0
            val completionTokens = res.usage?.completionTokens ?: 0
            val totalTokens = res.usage?.totalTokens ?: 0
            log.trace(
                "Chat token usage. Prompt: $promptTokens Completion: $completionTokens Total: $totalTokens")
          }
          .choices
          .mapNotNull { choice -> choice.message.content }
          .joinToString("\n")
          .let { ChatContainer(it, openaiProperties.models.chat.name) }

  private fun ChatMessageRole.toChatRole(): ChatRole =
      when (this) {
        ChatMessageRole.USER -> ChatRole.User
        ChatMessageRole.SYSTEM -> ChatRole.System
      }
}
