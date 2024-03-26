package us.craigmiller160.tolkienai.server.ai.ingestion.service

import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class DataIngestionService(
    private val rawSourceParsingService: RawSourceParsingService,
    private val environment: Environment
) {
  fun ingest(dryRun: Boolean) {
    if (dryRun && !dryRunAllowed()) {
      throw IllegalArgumentException("Dry runs are only allowed in the dev environment")
    }
    rawSourceParsingService.parseSilmarillion(dryRun)
  }

  private fun dryRunAllowed(): Boolean = environment.matchesProfiles("dev")
}
