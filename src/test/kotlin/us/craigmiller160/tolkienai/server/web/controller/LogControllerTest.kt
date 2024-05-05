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
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
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
import us.craigmiller160.tolkienai.server.testcore.DefaultUsers
import us.craigmiller160.tolkienai.server.testcore.IntegrationTest
import us.craigmiller160.tolkienai.server.web.type.ChatExecutionTime
import us.craigmiller160.tolkienai.server.web.type.ChatExplanation
import us.craigmiller160.tolkienai.server.web.type.ChatLogSearchResponse
import us.craigmiller160.tolkienai.server.web.type.ChatResponse
import us.craigmiller160.tolkienai.server.web.type.IngestionLogSearchResponse
import us.craigmiller160.tolkienai.server.web.type.TIMESTAMP_FORMATTER

@IntegrationTest
class LogControllerTest
@Autowired
constructor(
    private val ingestionLogRepo: IngestionLogRepository,
    private val chatLogRepo: ChatLogRepository,
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val defaultUsers: DefaultUsers
) {
  companion object {
    @JvmStatic
    fun ingestionLogArgs(): Stream<Arguments> =
        Stream.of(
                IngestionLogArgs(
                    responseIndexes = (0 until 10).toList(),
                    page = 0,
                    start = null,
                    end = null,
                    totalMatchingRecords = 100),
                IngestionLogArgs(
                    responseIndexes = (10 until 20).toList(),
                    page = 1,
                    start = null,
                    end = null,
                    totalMatchingRecords = 100),
                IngestionLogArgs(
                    responseIndexes = (84 until 94).toList(),
                    page = 0,
                    start = null,
                    end = BASE_TIMESTAMP.plusHours(15),
                    totalMatchingRecords = 16),
                IngestionLogArgs(
                    responseIndexes = (0 until 5).toList(),
                    page = 0,
                    start = BASE_TIMESTAMP.plusHours(95),
                    end = null,
                    totalMatchingRecords = 5))
            .map { it.toArguments() }

    @JvmStatic
    fun chatLogArgs(): Stream<Arguments> =
        Stream.of(
                ChatLogArgs(
                    responseIndexes = (0 until 10).toList(),
                    page = 0,
                    group = null,
                    start = null,
                    end = null,
                    totalMatchingRecords = 100),
                ChatLogArgs(
                    responseIndexes = (10 until 20).toList(),
                    page = 1,
                    group = null,
                    start = null,
                    end = null,
                    totalMatchingRecords = 100),
                ChatLogArgs(
                    responseIndexes = (15 until 25).toList(),
                    page = 0,
                    group = null,
                    start = null,
                    end = BASE_TIMESTAMP.plusHours(15),
                    totalMatchingRecords = 85),
                ChatLogArgs(
                    responseIndexes = (0 until 5).toList(),
                    page = 0,
                    group = null,
                    start = BASE_TIMESTAMP.plusHours(5),
                    end = null,
                    totalMatchingRecords = 5),
                ChatLogArgs(
                    responseIndexes = listOf(0, 2, 4, 6, 8, 10, 12, 14, 16, 18),
                    page = 0,
                    group = FIRST_GROUP,
                    start = null,
                    end = null,
                    totalMatchingRecords = 50))
            .map { it.toArguments() }
  }

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
          .reversed()
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
                        group = if (index % 2 == 0) FIRST_GROUP else SECOND_GROUP,
                        executionTime =
                            ChatExecutionTime(
                                createQueryEmbeddingMillis = randomMillis(),
                                vectorSearchMillis = randomMillis(),
                                chatMillis = randomMillis(),
                                totalMillis = randomMillis())))
          }
          .reversed()
          .let { runBlocking { chatLogRepo.insertAllChatLogs(it) } }

  @BeforeEach
  fun setup() {
    clearData()
  }

  @AfterEach
  fun cleanup() {
    clearData()
  }

  @ParameterizedTest(name = "searching for chat logs between {1} and {2} in group {3} on page {0}")
  @MethodSource("chatLogArgs")
  fun `searching for chat logs`(
      page: Int,
      start: String?,
      end: String?,
      group: String?,
      responseIndexes: List<Int>,
      totalMatchingRecords: Long
  ) {
    val logs = createChatLogs()
    val expected =
        ChatLogSearchResponse(
            pageSize = 10,
            pageNumber = page,
            totalRecords = totalMatchingRecords,
            logs = responseIndexes.map { logs[it] })
    mockMvc
        .get("/logs/chat") {
          param("pageNumber", page.toString())
          param("pageSize", "10")
          start?.let { param("startTimestamp", it) }
          end?.let { param("endTimestamp", it) }
          group?.let { param("group", it) }
          header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
        }
        .andExpect {
          status { isOk() }
          content { json(objectMapper.writeValueAsString(expected)) }
        }
  }

  @ParameterizedTest(name = "searching for ingestion logs between {1} and {2} on page {0}")
  @MethodSource("ingestionLogArgs")
  fun `searching for ingestion logs`(
      page: Int,
      start: String?,
      end: String?,
      responseIndexes: List<Int>,
      totalMatchingRecords: Long
  ) {
    val logs = createIngestionLogs()
    val expected =
        IngestionLogSearchResponse(
            pageSize = 10,
            pageNumber = page,
            totalRecords = totalMatchingRecords,
            logs = responseIndexes.map { logs[it] })

    val actual =
        mockMvc
            .get("/logs/ingestion") {
              param("pageNumber", page.toString())
              param("pageSize", "10")
              start?.let { param("startTimestamp", it) }
              end?.let { param("endTimestamp", it) }
              header("Authorization", "Bearer ${defaultUsers.primaryUser.token}")
            }
            .andExpect { status { isOk() } }
            .andReturn()
            .response
            .contentAsString
            .let { objectMapper.readValue(it, IngestionLogSearchResponse::class.java) }
    assertThat(actual).isEqualTo(expected)
  }
}

private val faker = Faker()

private data class IngestionLogArgs(
    val responseIndexes: List<Int>,
    val totalMatchingRecords: Long,
    val page: Int,
    val start: ZonedDateTime? = null,
    val end: ZonedDateTime? = null
)

private fun IngestionLogArgs.toArguments(): Arguments =
    Arguments.of(
        page,
        start?.format(TIMESTAMP_FORMATTER),
        end?.format(TIMESTAMP_FORMATTER),
        responseIndexes,
        totalMatchingRecords)

private data class ChatLogArgs(
    val responseIndexes: List<Int>,
    val totalMatchingRecords: Long,
    val page: Int,
    val group: String? = null,
    val start: ZonedDateTime? = null,
    val end: ZonedDateTime? = null
)

private fun ChatLogArgs.toArguments(): Arguments =
    Arguments.of(
        page,
        start?.format(TIMESTAMP_FORMATTER),
        end?.format(TIMESTAMP_FORMATTER),
        group,
        responseIndexes,
        totalMatchingRecords)

private val BASE_TIMESTAMP =
    ZonedDateTime.of(LocalDate.of(2024, 1, 1), LocalTime.of(0, 0, 0, 0), ZoneId.of("UTC"))

private fun randomCount(): Int = Random.nextInt(0, 10_001)

private fun randomMillis(): Long = Random.nextLong(0, 1_000 * 60 * 10)

private fun recordCreationRange(): IntRange = (0 until 100)

private const val FIRST_GROUP = "first_group"
private const val SECOND_GROUP = "second_group"
