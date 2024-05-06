package us.craigmiller160.tolkienai.server.web.controller

import org.springdoc.core.annotations.ParameterObject
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import us.craigmiller160.tolkienai.server.web.service.LogService
import us.craigmiller160.tolkienai.server.web.type.ChatLogSearchRequest
import us.craigmiller160.tolkienai.server.web.type.ChatLogSearchResponse
import us.craigmiller160.tolkienai.server.web.type.IngestionLogSearchRequest
import us.craigmiller160.tolkienai.server.web.type.IngestionLogSearchResponse

@RestController
@RequestMapping("/logs")
class LogController(private val logService: LogService) {
  @GetMapping("/ingestion")
  fun searchForIngestionLogs(
      @ParameterObject request: IngestionLogSearchRequest
  ): IngestionLogSearchResponse = logService.searchForIngestionLogs(request)

  @GetMapping("/chat")
  fun searchForChatLogs(@ParameterObject request: ChatLogSearchRequest): ChatLogSearchResponse =
      logService.searchForChatLogs(request)
}
