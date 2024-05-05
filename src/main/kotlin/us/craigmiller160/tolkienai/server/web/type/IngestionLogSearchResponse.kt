package us.craigmiller160.tolkienai.server.web.type

import us.craigmiller160.tolkienai.server.data.entity.IngestionLog

data class IngestionLogSearchResponse(
    override val pageSize: Int,
    override val pageNumber: Int,
    override val totalRecords: Long,
    val logs: List<IngestionLog>
) : PageResponse
