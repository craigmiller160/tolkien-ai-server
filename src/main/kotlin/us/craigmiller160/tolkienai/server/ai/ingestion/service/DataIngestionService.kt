package us.craigmiller160.tolkienai.server.ai.ingestion.service

import kotlin.time.Duration.Companion.nanoseconds
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import us.craigmiller160.tolkienai.server.ai.dto.Tokens
import us.craigmiller160.tolkienai.server.ai.dto.floatEmbedding
import us.craigmiller160.tolkienai.server.ai.ingestion.service.parsing.RawSourceParsingService
import us.craigmiller160.tolkienai.server.ai.service.OpenAiService
import us.craigmiller160.tolkienai.server.ai.service.WeaviateService
import us.craigmiller160.tolkienai.server.data.entity.IngestionDetails
import us.craigmiller160.tolkienai.server.data.repository.IngestionLogRepository

@Service
class DataIngestionService(
    private val rawSourceParsingService: RawSourceParsingService,
    private val environment: Environment,
    private val openAiService: OpenAiService,
    private val weaviateService: WeaviateService,
    private val ingestionLogRepository: IngestionLogRepository
) {
  private val log = LoggerFactory.getLogger(javaClass)
  fun ingest(dryRun: Boolean) {
    if (dryRun && !dryRunAllowed()) {
      throw IllegalArgumentException("Dry runs are only allowed in the dev environment")
    }

    log.info("Beginning data ingestion")
    val start = System.nanoTime()

    val segments = rawSourceParsingService.parseSilmarillion(dryRun)
    if (dryRun) {
      log.info("Dry run enabled, aborting ingestion before creating/inserting embeddings")
      return
    }

    val totalCharacters = segments.sumOf { it.length }

    log.debug("Creating and inserting embeddings from segments. This might take a while.")
    val allTokens = runBlocking {
      segments
          .map { segment ->
            async {
              openAiService.createEmbedding(segment).let { embedding ->
                weaviateService.insertEmbedding(embedding.text, embedding.floatEmbedding)
                embedding.tokens
              }
            }
          }
          .awaitAll()
    }
    val end = System.nanoTime()
    val timeMillis = (end - start).nanoseconds.inWholeMilliseconds

    val totalTotals =
        allTokens.fold(Tokens(0, 0, 0)) { acc, elem ->
          acc.copy(
              prompt = acc.prompt + elem.prompt,
              completion = acc.completion + elem.completion,
              total = acc.total + elem.total)
        }

    val ingestionDetails =
        IngestionDetails(
            characters = totalCharacters,
            segments = segments.size,
            executionTimeMillis = timeMillis,
            tokens = totalTotals)
    runBlocking { ingestionLogRepository.insertIngestionDetails(ingestionDetails) }

    log.info("Data ingestion complete")
  }

  private fun dryRunAllowed(): Boolean = environment.matchesProfiles("dev")
}
