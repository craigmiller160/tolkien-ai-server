package us.craigmiller160.tolkienai.server.ai.ingestion.service

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.bufferedReader
import kotlin.streams.asSequence
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
    private const val PARSED_ONE_FILE = "parsed-1.txt"
    private const val WHITESPACE_CLEANED_UP_FILE = "whitespace-cleaned-up.txt"
  }

  private val log = LoggerFactory.getLogger(javaClass)

  fun parseSilmarillion() {
    log.info("Parsing raw Silmarillion text")
    val tempDirectory = prepareTempDirectory()
    cleanupWhiteSpace(tempDirectory)
    parseTwo(tempDirectory)
    log.info("Raw Silmarillion text is parsed")
  }

  private fun parseTwo(tempDirectory: Path) {
    log.debug("Performing second parsing of raw Silmarillion text")
    val segmentTempDirectory = Paths.get(tempDirectory.toString(), "segments")
    Paths.get(tempDirectory.toString(), PARSED_ONE_FILE).bufferedReader().use { reader ->
      reader
          .lines()
          .asSequence()
          .scan<String, Segment?>(null) { previousSegment, currentLine ->
            createOrUpdateSegment(previousSegment, currentLine)
          }
          .filterNotNull()
          // The last occurring record with the id will win
          .associateBy { it.id }
      // TODO parallelize and write out the segments
    }
    log.debug("Second parsing of raw Silmarillion text complete")
  }

  private fun cleanupWhiteSpace(tempDirectory: Path): String {
    log.debug("Cleaning up whitespace in Silmarillion text")
    return File(rawSourcesProperties.silmarillion)
        .bufferedReader()
        .readText()
        .split("\n")
        .scan<String, LineWrapper?>(null) { previousLineWrapper, currentLine ->
          lineToLineWrapper(previousLineWrapper?.line, currentLine)
        }
        .filterNotNull()
        .filter { it !is DeleteLine }
        .joinToString("\n") { it.toText() }
        .also {
          if (environment.matchesProfiles("dev")) {
            File(WHITESPACE_CLEANED_UP_FILE).bufferedWriter().write(it)
          }
          log.debug("Silmarillion text whitespace cleaned up")
        }
  }

  private fun prepareTempDirectory(): Path {
    val tempDirectoryPath = Paths.get(System.getProperty("user.dir"), "temp")
    if (Files.exists(tempDirectoryPath)) {
      Files.walk(tempDirectoryPath).forEach { Files.delete(it) }
    }
    Files.createDirectory(tempDirectoryPath)
    return tempDirectoryPath
  }
}
