package us.craigmiller160.tolkienai.server.web.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/logs")
class LogController {
  @GetMapping("/ingestion") fun getIngestionLogs(): Unit = TODO()

  @GetMapping("/chat") fun getChatLogs(): Unit = TODO()
}
