package us.craigmiller160.tolkienai.server.web.type

import java.util.UUID
import us.craigmiller160.tolkienai.server.ai.dto.ChatMessageContainer
import us.craigmiller160.tolkienai.server.ai.dto.Tokens

data class ChatExplanation(
    val query: List<ChatMessageContainer>,
    val embeddingMatches: List<String>
)

data class ChatExecutionTime(
    val createQueryEmbeddingMillis: Long,
    val vectorSearchMillis: Long,
    val chatMillis: Long,
    val totalMillis: Long
)

data class ChatResponse(
    val chatId: UUID,
    val model: String,
    val response: String,
    val explanation: ChatExplanation,
    val tokens: Tokens,
    val executionTime: ChatExecutionTime,
    val group: String? = null
)
