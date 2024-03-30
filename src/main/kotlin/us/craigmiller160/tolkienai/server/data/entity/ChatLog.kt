package us.craigmiller160.tolkienai.server.data.entity

import java.time.ZonedDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import us.craigmiller160.tolkienai.server.web.type.ChatResponse

@Document(collection = "chatLog")
data class ChatLog(
    val chat: ChatResponse,
    @field:Id val id: String? = null,
    val timestamp: ZonedDateTime = ZonedDateTime.now()
)
