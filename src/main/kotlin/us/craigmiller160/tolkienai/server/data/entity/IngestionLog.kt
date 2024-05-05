package us.craigmiller160.tolkienai.server.data.entity

import java.time.ZonedDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import us.craigmiller160.tolkienai.server.ai.dto.Tokens

data class IngestionDetails(
    val characters: Int,
    val segments: Int,
    val executionTimeMillis: Long,
    val tokens: Tokens
)

@Document(collection = INGESTION_LOG_COLLECTION)
data class IngestionLog(
    val details: IngestionDetails,
    val timestamp: ZonedDateTime = ZonedDateTime.now(),
    @field:Id val id: String? = null
)
