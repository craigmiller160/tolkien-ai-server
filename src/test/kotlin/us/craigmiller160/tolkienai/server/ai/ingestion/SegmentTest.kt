package us.craigmiller160.tolkienai.server.ai.ingestion

import java.util.stream.Stream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import us.craigmiller160.tolkienai.server.ai.ingestion.exception.InvalidSegmentException
import us.craigmiller160.tolkienai.server.ai.ingestion.service.Segment
import us.craigmiller160.tolkienai.server.ai.ingestion.service.TitleLine
import us.craigmiller160.tolkienai.server.ai.ingestion.service.createOrUpdateSegment

class SegmentTest {
  companion object {
    @JvmStatic
    fun createOrUpdateSegmentArgs(): Stream<CreateOrUpdateSegmentArg> {
      val baseSegment = Segment("TITLE", "Body", null)

      return Stream.of(
          CreateOrUpdateSegmentArg(
              null, "HELLO", Result.success(Segment("HELLO", "", TitleLine("HELLO")))),
          CreateOrUpdateSegmentArg(
              baseSegment,
              "HELLO",
              Result.success(
                  baseSegment.copy(
                      title = "TITLE HELLO", previousLineWrapper = TitleLine("HELLO")))),
          CreateOrUpdateSegmentArg(
              null,
              "World",
              Result.failure(
                  InvalidSegmentException(
                      "No previous segment with a title to append content to"))))
    }
  }
  @ParameterizedTest
  @MethodSource("createOrUpdateSegmentArgs")
  fun `creates or updates segment correctly`(arg: CreateOrUpdateSegmentArg) {
    val actualSegment = runCatching { createOrUpdateSegment(arg.previousSegment, arg.currentLine) }
    assertThat(actualSegment).isEqualTo(arg.expectedSegment)
  }

  data class CreateOrUpdateSegmentArg(
      val previousSegment: Segment?,
      val currentLine: String,
      val expectedSegment: Result<Segment>
  )
}
