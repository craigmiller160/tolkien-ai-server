package us.craigmiller160.tolkienai.server.web.controller

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.random.Random
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import us.craigmiller160.tolkienai.server.ai.dto.Tokens
import us.craigmiller160.tolkienai.server.data.entity.ChatLog
import us.craigmiller160.tolkienai.server.data.entity.IngestionDetails
import us.craigmiller160.tolkienai.server.data.entity.IngestionLog
import us.craigmiller160.tolkienai.server.data.repository.ChatLogRepository
import us.craigmiller160.tolkienai.server.data.repository.IngestionLogRepository
import us.craigmiller160.tolkienai.server.testcore.IntegrationTest
import us.craigmiller160.tolkienai.server.web.type.ChatExecutionTime
import us.craigmiller160.tolkienai.server.web.type.ChatResponse

@IntegrationTest
class LogControllerTest(
    private val ingestionLogRepo: IngestionLogRepository,
    private val chatLogRepo: ChatLogRepository
) {

  private fun clearData() = runBlocking {
    ingestionLogRepo.deleteAllIngestionLogs()
    chatLogRepo.deleteAllChatLogs()
  }

  private fun createIngestionLogs(): List<IngestionLog> =
      recordCreationRange()
          .map { index ->
            IngestionLog(
                timestamp = BASE_TIMESTAMP.plusHours(index.toLong()),
                details =
                    IngestionDetails(
                        characters = randomCount(),
                        segments = randomCount(),
                        executionTimeMillis = randomMillis(),
                        tokens = Tokens(prompt = 0, completion = 0, total = 0)))
          }
          .let { runBlocking { ingestionLogRepo.insertAllIngestionLogs(it) } }

  private fun createChatLogs(): List<ChatLog> =
      recordCreationRange()
          .map { index ->
            ChatLog(
                timestamp = BASE_TIMESTAMP.plusHours(index.toLong()),
                details =
                    ChatResponse(
                        chatId = UUID.randomUUID(),
                        model = "gpt-4",
                        response = TODO(),
                        explanation = TODO(),
                        tokens = Tokens(prompt = 0, completion = 0, total = 0),
                        group = TODO(),
                        executionTime =
                            ChatExecutionTime(
                                createQueryEmbeddingMillis = randomMillis(),
                                vectorSearchMillis = randomMillis(),
                                chatMillis = randomMillis(),
                                totalMillis = randomMillis())))
          }
          .let { runBlocking { chatLogRepo.insertAllChatLogs(it) } }

  @BeforeEach
  fun setup() {
    clearData()
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

private val BASE_TIMESTAMP =
    ZonedDateTime.of(LocalDate.of(2024, 1, 1), LocalTime.of(0, 0, 0), ZoneId.of("UTC"))

private fun randomCount(): Int = Random.nextInt(0, 10_001)

private fun randomMillis(): Long = Random.nextLong(0, 1_000 * 60 * 10)

private fun recordCreationRange(): IntRange = (0 until 100)
