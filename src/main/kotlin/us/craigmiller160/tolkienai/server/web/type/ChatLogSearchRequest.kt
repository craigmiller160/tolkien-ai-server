package us.craigmiller160.tolkienai.server.web.type

import java.time.ZonedDateTime
import org.springframework.format.annotation.DateTimeFormat

data class ChatLogSearchRequest(
    override val pageNumber: Int,
    override val pageSize: Int,
    @field:DateTimeFormat(pattern = TIMESTAMP_FORMAT) val startTimestamp: ZonedDateTime?,
    @field:DateTimeFormat(pattern = TIMESTAMP_FORMAT) val endTimestamp: ZonedDateTime?,
    val group: String?
) : PageRequest
