package us.craigmiller160.tolkienai.server.ai.service

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlin.time.Duration.Companion.nanoseconds
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import us.craigmiller160.tolkienai.server.ai.dto.ChatContainer
import us.craigmiller160.tolkienai.server.ai.dto.ChatMessageContainer
import us.craigmiller160.tolkienai.server.ai.dto.ChatMessageRole
import us.craigmiller160.tolkienai.server.ai.dto.EmbeddingContainer
import us.craigmiller160.tolkienai.server.ai.dto.Tokens
import us.craigmiller160.tolkienai.server.config.OpenaiProperties

@Service
class OpenAiService(
    private val openAiClient: OpenAI,
    private val openaiProperties: OpenaiProperties
) {
  private val log = LoggerFactory.getLogger(javaClass)
  suspend fun createEmbedding(text: String): EmbeddingContainer {
    val start = System.nanoTime()
    return ModelId(openaiProperties.models.embedding.name)
        .let { EmbeddingRequest(model = it, input = listOf(text)) }
        .let { openAiClient.embeddings(it) }
        .also { res ->
          val end = System.nanoTime()
          val promptTokens = res.usage.promptTokens ?: 0
          val totalTokens = res.usage.totalTokens ?: 0
          val millis = (end - start).nanoseconds.inWholeMilliseconds
          log.trace(
              "Embedding generated. Time: ${millis}ms. Prompt Tokens: $promptTokens. Total Tokens: $totalTokens")
        }
        .let { res ->
          val allEmbeddings = res.embeddings.flatMap { it.embedding }
          EmbeddingContainer(
              embedding = allEmbeddings,
              text = text,
              dimensions = openaiProperties.models.embedding.dimensions,
              tokens =
                  Tokens(
                      prompt = res.usage.promptTokens ?: 0,
                      completion = res.usage.completionTokens ?: 0,
                      total = res.usage.totalTokens ?: 0))
        }
  }

  suspend fun createChat(messages: List<ChatMessageContainer>): ChatContainer =
      ChatCompletionRequest(
              model = ModelId(openaiProperties.models.chat.name),
              messages =
                  messages.map { (role, content) ->
                    ChatMessage(role = role.toChatRole(), content = content)
                  })
          .let { openAiClient.chatCompletion(it) }
          .let { res ->
            val promptTokens = res.usage?.promptTokens ?: 0
            val completionTokens = res.usage?.completionTokens ?: 0
            val totalTokens = res.usage?.totalTokens ?: 0
            log.trace(
                "Chat token usage. Prompt: $promptTokens Completion: $completionTokens Total: $totalTokens")

            val responseString =
                res.choices.mapNotNull { choice -> choice.message.content }.joinToString("\n")
            ChatContainer(
                response = responseString,
                model = openaiProperties.models.chat.name,
                tokens =
                    Tokens(
                        total = totalTokens, prompt = promptTokens, completion = completionTokens))
          }

  private fun ChatMessageRole.toChatRole(): ChatRole =
      when (this) {
        ChatMessageRole.USER -> ChatRole.User
        ChatMessageRole.SYSTEM -> ChatRole.System
      }
}
