package us.craigmiller160.tolkienai.server.data.migration

import java.time.ZonedDateTime
import org.springframework.data.annotation.Id

data class MigrationRecord(
    @Id val id: String,
    val index: Int,
    val name: String,
    val hash: String,
    val timestamp: ZonedDateTime
)
