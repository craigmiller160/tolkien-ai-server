package us.craigmiller160.tolkienai.server.web.type

import java.util.UUID

data class ChatExplanation(val query: String, val embeddingMatches: List<String>)

data class ChatResponse(val chatId: UUID, val response: String, val explanation: ChatExplanation)
