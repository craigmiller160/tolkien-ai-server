package us.craigmiller160.tolkienai.server.web.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import us.craigmiller160.tolkienai.server.web.type.ChatResponse

@Document(collection = "chat_log")
data class ChatLog(val chat: ChatResponse, @field:Id val id: String? = null)
