package us.craigmiller160.tolkienai.server.web.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import us.craigmiller160.tolkienai.server.ai.ingestion.service.DataIngestionService
import us.craigmiller160.tolkienai.server.web.type.IngestDataRequest

@RestController
@RequestMapping("/data-ingestion")
class DataIngestionController(private val dataIngestionService: DataIngestionService) {
  @PostMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  //  @PreAuthorize("hasAuthority('ADMIN')")
  fun ingestData(@RequestBody request: IngestDataRequest) {
    dataIngestionService.ingestSilmarillion(request.dryRun)
  }
}
