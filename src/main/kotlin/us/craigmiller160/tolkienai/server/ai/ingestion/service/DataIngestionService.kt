package us.craigmiller160.tolkienai.server.ai.ingestion.service

import org.springframework.stereotype.Service

@Service
class DataIngestionService(private val rawSourceParsingService: RawSourceParsingService) {
  fun ingestSilmarillion(dryRun: Boolean) {
    rawSourceParsingService.parseSilmarillion()
  }
}
