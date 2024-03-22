package us.craigmiller160.tolkienai.server.ai.ingestion

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

enum class IngestionStatus {
    COMPLETE,
    PENDING
}

@Document(collection = "ingestion_metadata")
data class IngestionMetadata(
    @Id
    val id: String,
    val silmarillionStatus: IngestionStatus = IngestionStatus.PENDING
)
