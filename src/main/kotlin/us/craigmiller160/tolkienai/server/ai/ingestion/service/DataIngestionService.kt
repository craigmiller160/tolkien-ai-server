package us.craigmiller160.tolkienai.server.ai.ingestion.service

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import us.craigmiller160.tolkienai.server.ai.dto.floatEmbedding
import us.craigmiller160.tolkienai.server.ai.ingestion.service.parsing.RawSourceParsingService
import us.craigmiller160.tolkienai.server.ai.service.OpenAiService
import us.craigmiller160.tolkienai.server.ai.service.WeaviateService

@Service
class DataIngestionService(
    private val rawSourceParsingService: RawSourceParsingService,
    private val environment: Environment,
    private val openAiService: OpenAiService,
    private val weaviateService: WeaviateService
) {
  fun ingest(dryRun: Boolean) {
    if (dryRun && !dryRunAllowed()) {
      throw IllegalArgumentException("Dry runs are only allowed in the dev environment")
    }

    runBlocking {
      weaviateService.createSilmarillionClass()

      rawSourceParsingService
          .parseSilmarillion(dryRun)
          .map { segment ->
            async {
              openAiService.createEmbedding(segment).let { embedding ->
                weaviateService.insertEmbedding(embedding.text, embedding.floatEmbedding)
              }
            }
          }
          .awaitAll()
    }
  }

  private fun dryRunAllowed(): Boolean = environment.matchesProfiles("dev")
}
