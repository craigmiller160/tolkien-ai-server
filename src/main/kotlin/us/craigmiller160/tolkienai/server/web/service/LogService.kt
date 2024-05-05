package us.craigmiller160.tolkienai.server.web.service

import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import us.craigmiller160.tolkienai.server.data.repository.ChatLogRepository
import us.craigmiller160.tolkienai.server.data.repository.IngestionLogRepository
import us.craigmiller160.tolkienai.server.web.type.ChatLogSearchRequest
import us.craigmiller160.tolkienai.server.web.type.ChatLogSearchResponse
import us.craigmiller160.tolkienai.server.web.type.IngestionLogSearchRequest
import us.craigmiller160.tolkienai.server.web.type.IngestionLogSearchResponse
import us.craigmiller160.tolkienai.server.web.type.page

@Service
class LogService(
    private val chatLogRepository: ChatLogRepository,
    private val ingestionLogRepository: IngestionLogRepository
) {
  fun searchForIngestionLogs(request: IngestionLogSearchRequest): IngestionLogSearchResponse =
      runBlocking {
        val results =
            ingestionLogRepository.searchForIngestionLogs(
                request.page, request.startTimestamp, request.endTimestamp)
        val count =
            ingestionLogRepository.getCountForSearchForIngestionLogs(
                request.startTimestamp, request.endTimestamp)
        IngestionLogSearchResponse(
            pageNumber = request.pageNumber,
            pageSize = request.pageSize,
            totalRecords = count,
            logs = results)
      }

  fun searchForChatLogs(request: ChatLogSearchRequest): ChatLogSearchResponse {
    return ChatLogSearchResponse(pageNumber = 0, pageSize = 0, logs = listOf(), totalRecords = 0)
  }
}
