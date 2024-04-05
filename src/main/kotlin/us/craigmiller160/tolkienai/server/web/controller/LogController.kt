package us.craigmiller160.tolkienai.server.web.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import us.craigmiller160.tolkienai.server.web.type.ChatLogSearchRequest
import us.craigmiller160.tolkienai.server.web.type.ChatLogSearchResponse
import us.craigmiller160.tolkienai.server.web.type.IngestionLogSearchRequest
import us.craigmiller160.tolkienai.server.web.type.IngestionLogSearchResponse

@RestController
@RequestMapping("/logs")
class LogController {
  @GetMapping("/ingestion")
  fun searchForIngestionLogs(request: IngestionLogSearchRequest): IngestionLogSearchResponse =
      TODO()

  @GetMapping("/chat")
  fun searchForChatLogs(request: ChatLogSearchRequest): ChatLogSearchResponse = TODO()
}
