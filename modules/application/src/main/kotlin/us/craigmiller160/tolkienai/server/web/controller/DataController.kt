package us.craigmiller160.tolkienai.server.web.controller

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import us.craigmiller160.tolkienai.server.ai.ingestion.service.DataIngestionService
import us.craigmiller160.tolkienai.server.web.service.DataService
import us.craigmiller160.tolkienai.server.web.type.IngestDataRequest
import us.craigmiller160.tolkienai.server.web.type.RecordCountResponse

@RestController
@RequestMapping("/data")
class DataController(
    private val dataIngestionService: DataIngestionService,
    private val dataService: DataService
) {
  @PostMapping("/ingest")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAuthority('ROLE_admin')")
  fun ingestData(@RequestBody request: IngestDataRequest) {
    dataIngestionService.ingest(request.dryRun)
  }

  @GetMapping("/count") fun getRecordCount(): RecordCountResponse = dataService.getRecordCount()

  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  fun deleteAllRecords() = dataService.deleteAllRecords()
}
