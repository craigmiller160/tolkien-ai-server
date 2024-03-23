package us.craigmiller160.tolkienai.server.ai.ingestion

import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import us.craigmiller160.tolkienai.server.ai.ingestion.exception.InvalidSegmentException
import us.craigmiller160.tolkienai.server.ai.ingestion.service.ParagraphLine
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
              baseSegment, "HELLO", Result.success(Segment("HELLO", "", TitleLine("HELLO")))),
          CreateOrUpdateSegmentArg(
              baseSegment.copy(content = "", previousLineWrapper = TitleLine("TITLE")),
              "HELLO",
              Result.success(
                  Segment(
                      title = "TITLE HELLO",
                      content = "",
                      previousLineWrapper = TitleLine("HELLO")))),
          CreateOrUpdateSegmentArg(
              baseSegment,
              "World",
              Result.success(
                  baseSegment.copy(
                      content = "World", previousLineWrapper = ParagraphLine("World")))),
          CreateOrUpdateSegmentArg(
              null,
              "World",
              Result.failure(
                  InvalidSegmentException(
                      "No previous segment with a title to append content to"))),
          CreateOrUpdateSegmentArg(
              baseSegment.copy(title = ""),
              "World",
              Result.failure(
                  InvalidSegmentException("Previous segment has no title, cannot append content"))),
          CreateOrUpdateSegmentArg(
              baseSegment,
              "",
              Result.failure(InvalidSegmentException("Invalid line wrapper: DeleteLine"))),
          CreateOrUpdateSegmentArg(
              baseSegment.copy(previousLineWrapper = TitleLine("TITLE")),
              "",
              Result.failure(InvalidSegmentException("Invalid line wrapper: NewLine"))))
    }
  }
  @ParameterizedTest
  @MethodSource("createOrUpdateSegmentArgs")
  fun `creates or updates segment correctly`(arg: CreateOrUpdateSegmentArg) {
    val actualSegment = runCatching { createOrUpdateSegment(arg.previousSegment, arg.currentLine) }
    if (arg.expectedSegment.isSuccess) {
      actualSegment.shouldBeSuccess { actual ->
        actual.shouldBe(arg.expectedSegment.getOrThrow().copy(id = actual.id))
      }
    } else {
      actualSegment
          .shouldBeFailure<InvalidSegmentException>()
          .message
          .shouldBe(arg.expectedSegment.exceptionOrNull()?.message)
    }
  }

  data class CreateOrUpdateSegmentArg(
      val previousSegment: Segment?,
      val currentLine: String,
      val expectedSegment: Result<Segment>
  )
}
