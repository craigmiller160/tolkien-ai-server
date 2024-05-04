package us.craigmiller160.tolkienai.server.web.service

import org.springframework.stereotype.Service
import us.craigmiller160.tolkienai.server.data.repository.ChatLogRepository
import us.craigmiller160.tolkienai.server.data.repository.IngestionLogRepository
import us.craigmiller160.tolkienai.server.web.type.ChatLogSearchRequest
import us.craigmiller160.tolkienai.server.web.type.ChatLogSearchResponse
import us.craigmiller160.tolkienai.server.web.type.IngestionLogSearchRequest
import us.craigmiller160.tolkienai.server.web.type.IngestionLogSearchResponse

@Service
class LogService(
    private val chatLogRepository: ChatLogRepository,
    private val ingestionLogRepository: IngestionLogRepository
) {
  fun searchForIngestionLogs(request: IngestionLogSearchRequest): IngestionLogSearchResponse =
      TODO()

  fun searchForChatLogs(request: ChatLogSearchRequest): ChatLogSearchResponse = TODO()
}
