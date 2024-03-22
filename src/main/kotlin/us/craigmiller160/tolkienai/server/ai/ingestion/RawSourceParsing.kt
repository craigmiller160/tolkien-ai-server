package us.craigmiller160.tolkienai.server.ai.ingestion

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import us.craigmiller160.tolkienai.server.config.RawSourcesProperties
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.bufferedWriter
import kotlin.streams.asSequence

@Component
class RawSourceParsing(
    private val rawSourcesProperties: RawSourcesProperties
) {
    companion object {
        private const val FULL_PARSED_FILE = "parsed.txt"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun parse() {
        log.info("Parsing raw Silmarillion text")
        val tempDirectory = prepareTempDirectory()
        Paths.get(tempDirectory.toString(), FULL_PARSED_FILE).bufferedWriter().use { writer ->
            File(rawSourcesProperties.silmarillion).bufferedReader().use { reader ->
                reader.lines()
                    .asSequence()
                    .scan<String, LineWrapper?>(null) { previousLineWrapper, currentLine ->
                        lineToLineWrapper(previousLineWrapper?.line, currentLine)
                    }
                    .filterNotNull()
                    .filter { it !is DeleteLine }
                    .forEach { lineWrapper ->
                        writer.write(lineWrapper.toText())
                    }
            }
        }
        log.info("Raw Silmarillion text is parsed")
    }

    private fun prepareTempDirectory(): Path {
        val tempDirectoryPath = Paths.get(System.getProperty("user.dir"), "temp")
        if (Files.exists(tempDirectoryPath)) {
            Files.walk(tempDirectoryPath)
                .forEach { Files.delete(it) }
        }
        Files.createDirectory(tempDirectoryPath)
        return tempDirectoryPath
    }
}