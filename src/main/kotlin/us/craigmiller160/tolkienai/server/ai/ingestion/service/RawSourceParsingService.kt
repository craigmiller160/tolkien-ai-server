package us.craigmiller160.tolkienai.server.ai.ingestion.service

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
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
  }

  private val log = LoggerFactory.getLogger(javaClass)

  fun parseSilmarillion() {
    log.info("Parsing raw Silmarillion text")
    prepareDebugDirectory()
    cleanupWhiteSpace().let { createSegments(it) }

    log.info("Raw Silmarillion text is parsed")
  }

  private fun createSegments(text: String): List<String> {
    log.debug("Converting Silmarillion text into segments")
    return text
        .lines()
        .scan<String, Segment?>(null) { previousSegment, currentLine ->
          createOrUpdateSegment(previousSegment, currentLine)
        }
        .filterNotNull()
        // The last occurring record with the id will win
        .associateBy { it.id }
        .values
        .map { it.toText() }
        .also { segments ->
          if (debugOutputEnabled()) {
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

  private fun cleanupWhiteSpace(): String {
    log.debug("Cleaning up whitespace in Silmarillion text")
    return File(rawSourcesProperties.silmarillion)
        .bufferedReader()
        .readText()
        .lines()
        .scan<String, LineWrapper?>(null) { previousLineWrapper, currentLine ->
          lineToLineWrapper(previousLineWrapper?.line, currentLine)
        }
        .filterNotNull()
        .filter { it !is DeleteLine }
        .joinToString("\n") { it.toText() }
        .also {
          if (debugOutputEnabled()) {
            Files.writeString(WHITESPACE_CLEANED_UP_FILE, it)
          }
          log.debug("Silmarillion text whitespace cleaned up")
        }
  }

  private fun debugOutputEnabled(): Boolean = environment.matchesProfiles("dev")

  private fun prepareDebugDirectory() {
    if (Files.exists(DEBUG_DIRECTORY)) {
      Files.walk(DEBUG_DIRECTORY).forEach { Files.delete(it) }
    }
    Files.createDirectory(DEBUG_DIRECTORY)
    Files.createDirectory(DEBUG_SEGMENTS_DIRECTORY)
  }
}
