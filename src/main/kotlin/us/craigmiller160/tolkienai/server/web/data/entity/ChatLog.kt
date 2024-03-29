package us.craigmiller160.tolkienai.server.web.data.entity

import java.util.UUID
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "chat_log") data class ChatLog(@field:Id val id: String, val chatId: UUID)
