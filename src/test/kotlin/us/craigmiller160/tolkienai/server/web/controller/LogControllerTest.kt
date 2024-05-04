package us.craigmiller160.tolkienai.server.web.controller

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.random.Random
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import us.craigmiller160.tolkienai.server.ai.dto.Tokens
import us.craigmiller160.tolkienai.server.data.entity.IngestionDetails
import us.craigmiller160.tolkienai.server.data.entity.IngestionLog
import us.craigmiller160.tolkienai.server.data.repository.ChatLogRepository
import us.craigmiller160.tolkienai.server.data.repository.IngestionLogRepository
import us.craigmiller160.tolkienai.server.testcore.IntegrationTest

@IntegrationTest
class LogControllerTest(
    private val ingestionLogRepo: IngestionLogRepository,
    private val chatLogRepo: ChatLogRepository
) {
  companion object {
    private val BASE_TIMESTAMP =
        ZonedDateTime.of(LocalDate.of(2024, 1, 1), LocalTime.of(0, 0, 0), ZoneId.of("UTC"))
  }

  private fun clearData() = runBlocking {
    ingestionLogRepo.deleteAllIngestionLogs()
    chatLogRepo.deleteAllChatLogs()
  }

  private fun randomCount(): Int = Random.nextInt(0, 10_001)

  private fun randomMillis(): Long = Random.nextLong()

  fun createIngestionLogs() {
    (0 until 100).map { index ->
      IngestionLog(
          timestamp = BASE_TIMESTAMP.plusHours(index.toLong()),
          details =
              IngestionDetails(
                  characters = randomCount(),
                  segments = randomCount(),
                  executionTimeMillis = randomMillis(),
                  tokens = Tokens(prompt = 0, completion = 0, total = 0)))
    }
  }

  @BeforeEach
  fun setup() {
    clearData()
    createIngestionLogs()
  }

  @AfterEach
  fun cleanup() {
    clearData()
  }

  @Test
  fun `all options for getting chat logs`() {
    TODO()
  }

  @Test
  fun `all options for getting ingestion logs`() {
    TODO()
  }
}
