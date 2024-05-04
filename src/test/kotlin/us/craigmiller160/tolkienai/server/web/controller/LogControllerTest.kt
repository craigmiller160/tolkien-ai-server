package us.craigmiller160.tolkienai.server.web.controller

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import us.craigmiller160.tolkienai.server.data.repository.ChatLogRepository
import us.craigmiller160.tolkienai.server.data.repository.IngestionLogRepository
import us.craigmiller160.tolkienai.server.testcore.IntegrationTest

@IntegrationTest
class LogControllerTest(
    private val ingestionLogRepo: IngestionLogRepository,
    private val chatLogRepo: ChatLogRepository
) {

  @BeforeEach fun setup() {}

  @AfterEach fun cleanup() {}

  @Test
  fun `all options for getting chat logs`() {
    TODO()
  }

  @Test
  fun `all options for getting ingestion logs`() {
    TODO()
  }
}
