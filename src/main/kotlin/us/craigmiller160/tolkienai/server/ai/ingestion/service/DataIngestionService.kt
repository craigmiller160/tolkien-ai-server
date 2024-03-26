package us.craigmiller160.tolkienai.server.ai.ingestion.service

import com.aallam.openai.client.OpenAI
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import us.craigmiller160.tolkienai.server.ai.ingestion.service.parsing.RawSourceParsingService

@Service
class DataIngestionService(
    private val rawSourceParsingService: RawSourceParsingService,
    private val environment: Environment,
    private val openAiClient: OpenAI
) {
  fun ingest(dryRun: Boolean) {
    if (dryRun && !dryRunAllowed()) {
      throw IllegalArgumentException("Dry runs are only allowed in the dev environment")
    }
    val segments = rawSourceParsingService.parseSilmarillion(dryRun)
  }

  private fun dryRunAllowed(): Boolean = environment.matchesProfiles("dev")
}
