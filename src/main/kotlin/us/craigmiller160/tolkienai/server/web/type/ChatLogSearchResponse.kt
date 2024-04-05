package us.craigmiller160.tolkienai.server.web.type

import us.craigmiller160.tolkienai.server.data.entity.ChatLog

data class ChatLogSearchResponse(
    override val totalRecords: Int,
    override val pageNumber: Int,
    override val pageSize: Int,
    val logs: List<ChatLog>
) : PageResponse
