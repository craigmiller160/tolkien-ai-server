package us.craigmiller160.tolkienai.server.web.type

import java.time.ZonedDateTime

data class IngestionLogSearchRequest(
    override val pageNumber: Int,
    override val pageSize: Int,
    val startTimestamp: ZonedDateTime?, // TODO need to define format
    val endTimestamp: ZonedDateTime? // TODO need to define format
) : PageRequest
