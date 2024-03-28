package us.craigmiller160.tolkienai.server.web.type

import java.util.UUID

data class ChatResponse(val chatId: UUID, val response: String)
