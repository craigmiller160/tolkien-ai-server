package us.craigmiller160.tolkienai.server.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import java.util.stream.Stream
import kotlin.random.Random
import kotlinx.coroutines.runBlocking
import net.datafaker.Faker
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import us.craigmiller160.tolkienai.server.ai.dto.ChatMessageContainer
import us.craigmiller160.tolkienai.server.ai.dto.ChatMessageRole
import us.craigmiller160.tolkienai.server.ai.dto.Tokens
import us.craigmiller160.tolkienai.server.data.entity.ChatLog
import us.craigmiller160.tolkienai.server.data.entity.IngestionDetails
import us.craigmiller160.tolkienai.server.data.entity.IngestionLog
import us.craigmiller160.tolkienai.server.data.repository.ChatLogRepository
import us.craigmiller160.tolkienai.server.data.repository.IngestionLogRepository
import us.craigmiller160.tolkienai.server.testcore.IntegrationTest
import us.craigmiller160.tolkienai.server.web.type.ChatExecutionTime
import us.craigmiller160.tolkienai.server.web.type.ChatExplanation
import us.craigmiller160.tolkienai.server.web.type.ChatResponse
import us.craigmiller160.tolkienai.server.web.type.IngestionLogSearchResponse

@IntegrationTest
class LogControllerTest
@Autowired
constructor(
    private val ingestionLogRepo: IngestionLogRepository,
    private val chatLogRepo: ChatLogRepository,
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {
  companion object {
    @JvmStatic
    fun ingestionLogArgs(): Stream<Arguments> {
      return Stream.of(
              IngestionLogArgs(
                  responseIndexes = listOf(),
                  page = 0,
                  start = ZonedDateTime.now(),
                  end = ZonedDateTime.now(),
                  totalMatchingRecords = 10))
          .map { it.toArguments() }
    }
  }

  private val faker = Faker()

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
            val message =
                ChatMessageContainer(role = ChatMessageRole.USER, message = faker.text().text(50))
            val explanation =
                ChatExplanation(
                    query = listOf(message), embeddingMatches = listOf(faker.text().text(50)))
            ChatLog(
                timestamp = BASE_TIMESTAMP.plusHours(index.toLong()),
                details =
                    ChatResponse(
                        chatId = UUID.randomUUID(),
                        model = "gpt-4",
                        response = faker.text().text(50),
                        explanation = explanation,
                        tokens = Tokens(prompt = 0, completion = 0, total = 0),
                        group = "test",
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
  fun `searching for chat logs`() {
    val logs = createChatLogs()
    TODO()
  }

  @ParameterizedTest(name = "searching for ingestion logs between {1} and {2} on page {0}")
  @MethodSource("ingestionLogArgs")
  fun `searching for ingestion logs`(
      page: Int,
      start: ZonedDateTime?,
      end: ZonedDateTime?,
      responseIndexes: List<Int>,
      totalMatchingRecords: Int
  ) {
    val logs = createIngestionLogs()
    val expected =
        IngestionLogSearchResponse(
            pageSize = 10,
            pageNumber = page,
            totalRecords = totalMatchingRecords,
            logs = responseIndexes.map { logs[it] })
    mockMvc
        .get("/logs/ingestion") {
          param("pageNumber", page.toString())
          param("pageSize", "10")
          start?.let { param("startTimestamp", it.toString()) }
          end?.let { param("endTimestamp", it.toString()) }
        }
        .andExpect {
          status { isOk() }
          content { json(objectMapper.writeValueAsString(expected)) }
        }
  }
}

private data class IngestionLogArgs(
    val responseIndexes: List<Int>,
    val totalMatchingRecords: Int,
    val page: Int,
    val start: ZonedDateTime? = null,
    val end: ZonedDateTime? = null
)

private fun IngestionLogArgs.toArguments(): Arguments =
    Arguments.of(page, start, end, responseIndexes, totalMatchingRecords)

private val BASE_TIMESTAMP =
    ZonedDateTime.of(LocalDate.of(2024, 1, 1), LocalTime.of(0, 0, 0), ZoneId.of("UTC"))

private fun randomCount(): Int = Random.nextInt(0, 10_001)

private fun randomMillis(): Long = Random.nextLong(0, 1_000 * 60 * 10)

private fun recordCreationRange(): IntRange = (0 until 100)
