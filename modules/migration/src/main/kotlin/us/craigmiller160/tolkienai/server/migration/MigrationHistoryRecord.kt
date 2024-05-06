package us.craigmiller160.tolkienai.server.migration

import java.time.ZonedDateTime
import org.springframework.data.annotation.Id

data class MigrationHistoryRecord(
    val index: Int,
    val version: String,
    val name: String,
    val hash: String,
    val timestamp: ZonedDateTime = ZonedDateTime.now(),
    @Id val id: String? = null,
)
