package us.craigmiller160.tolkienai.server.data.entity

import java.time.ZonedDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

data class IngestionDetails(val characters: Int, val segments: Int, val tokens: Tokens)

@Document(collection = "ingestionLog")
data class IngestionLog(
    val details: IngestionDetails,
    val timestamp: ZonedDateTime = ZonedDateTime.now(),
    @field:Id val id: String? = null
)
