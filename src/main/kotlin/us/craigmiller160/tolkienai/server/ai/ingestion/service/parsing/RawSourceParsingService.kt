package us.craigmiller160.tolkienai.server.ai.ingestion.service.parsing

import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.readText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import us.craigmiller160.tolkienai.server.config.RawSourcesProperties

@Service
class RawSourceParsingService(
    private val rawSourcesProperties: RawSourcesProperties,
    private val environment: Environment
) {
  companion object {
    private val DEBUG_DIRECTORY = Paths.get(System.getProperty("user.dir"), "temp")
    private val DEBUG_SEGMENTS_DIRECTORY = DEBUG_DIRECTORY.resolve("segments")
    private val WHITESPACE_CLEANED_UP_FILE = DEBUG_DIRECTORY.resolve("whitespace-cleaned.txt")
    private val EXCLUDED_LINE_RANGE_REGEX = Regex("(?<start>\\d+)-(?<end>\\d+)")
  }

  private val log = LoggerFactory.getLogger(javaClass)

  fun parseSilmarillion(dryRun: Boolean) {
    log.info("Parsing raw Silmarillion text")
    prepareDebugDirectory()
    Paths.get(rawSourcesProperties.silmarillion.path)
        .readText()
        .let { excludeLines(it) }
        .let { cleanupWhiteSpace(it, dryRun) }
        .let { createSegments(it, dryRun) }
        .also { log.info("Raw Silmarillion text is parsed") }
  }

  private fun excludeLines(text: String): String {
    log.debug("Excluding lines from raw Silmarillion text")
    val linesToExclude =
        rawSourcesProperties.silmarillion.excludeLines.map { excludedLine ->
          val match = EXCLUDED_LINE_RANGE_REGEX.matchEntire(excludedLine)
          val start = match?.groups?.get("start")?.value?.toInt() ?: excludedLine.toInt()
          val end = match?.groups?.get("end")?.value?.toInt() ?: excludedLine.toInt()
          start to end
        }
    return text
        .lines()
        .filterIndexed { index, _ ->
          val modifiedIndex = index + 1
          linesToExclude.find { (start, end) -> modifiedIndex in start..end } == null
        }
        .joinToString("\n")
        .also { log.debug("Lines from raw Silmarillion text excluded") }
  }

  private fun createSegments(text: String, dryRun: Boolean): List<String> {
    log.debug("Converting Silmarillion text into segments")
    return text
        .lines()
        .scan<String, Segment?>(null) { previousSegment, currentLine ->
          createOrUpdateSegment(previousSegment, currentLine)
        }
        .filterNotNull()
        // This will filter out duplicates because the most up-to-date record will win
        .associateBy { it.id }
        .values
        .map { it.toText() }
        .also { segments ->
          if (dryRun) {
            runBlocking {
              segments
                  .mapIndexed { index, segment ->
                    async(Dispatchers.IO) {
                      val segmentFile =
                          DEBUG_SEGMENTS_DIRECTORY.resolve(
                              "segment-${index.toString().padStart(10, '0')}.txt")
                      Files.writeString(segmentFile, segment)
                    }
                  }
                  .awaitAll()
            }
          }
          log.debug("Silmarillion text converted into segments")
        }
  }

  private fun cleanupWhiteSpace(text: String, dryRun: Boolean): String {
    log.debug("Cleaning up whitespace in Silmarillion text")
    return text
        .lines()
        .scan<String, LineWrapper?>(null) { previousLineWrapper, currentLine ->
          lineToLineWrapper(previousLineWrapper?.line, currentLine)
        }
        .filterNotNull()
        .filter { it !is DeleteLine }
        .joinToString("") { it.toText() }
        .also {
          if (dryRun) {
            Files.writeString(WHITESPACE_CLEANED_UP_FILE, it)
          }
          log.debug("Silmarillion text whitespace cleaned up")
        }
  }

  private fun prepareDebugDirectory() {
    if (Files.exists(DEBUG_DIRECTORY)) {
      Files.walk(DEBUG_DIRECTORY).sorted(Comparator.reverseOrder()).forEach { Files.delete(it) }
    }
    Files.createDirectory(DEBUG_DIRECTORY)
    Files.createDirectory(DEBUG_SEGMENTS_DIRECTORY)
  }
}
