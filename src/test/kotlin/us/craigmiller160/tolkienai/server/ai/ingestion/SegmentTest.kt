package us.craigmiller160.tolkienai.server.ai.ingestion

import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import us.craigmiller160.tolkienai.server.ai.ingestion.exception.InvalidSegmentException
import us.craigmiller160.tolkienai.server.ai.ingestion.service.Segment
import us.craigmiller160.tolkienai.server.ai.ingestion.service.SegmentType
import us.craigmiller160.tolkienai.server.ai.ingestion.service.createOrUpdateSegment

class SegmentTest {
  companion object {
    @JvmStatic
    fun createOrUpdateSegmentArgs(): Stream<CreateOrUpdateSegmentArg> {
      //      val baseSegment = Segment("TITLE", "Body", null)

      // If the line is a title, and the previous line is a title, append to the previous title
      // If the line is a NewLine or a DeleteLine, it's an error
      // If the line is a title, and the previous line is not, start a new Segment with the title
      // If the line is a paragraph, and there is no previous segment, it's an error
      // If the line is a paragraph, and the previous segment has no title, it's an error
      // If the line is a paragraph, and the previous segment has a title and content, start a new
      // Segment with the previous title and new content
      // If the line is a paragraph, and the previous segment has no content but has a title, append
      // to the previous Segment
      // If the line is a paragraph, and the previous segment has content & a title, but the content
      // doesn't have an "ending", append to previous content

      //      return Stream.of(
      //          CreateOrUpdateSegmentArg(
      //              null, "HELLO", Result.success(Segment("HELLO", "", TitleLine("HELLO")))),
      //          CreateOrUpdateSegmentArg(
      //              baseSegment, "HELLO", Result.success(Segment("HELLO", "",
      // TitleLine("HELLO")))),
      //          CreateOrUpdateSegmentArg(
      //              baseSegment.copy(content = "", previousLineWrapper = TitleLine("TITLE")),
      //              "HELLO",
      //              Result.success(Segment("TITLE HELLO", "", TitleLine("HELLO")))),
      //          CreateOrUpdateSegmentArg(
      //              baseSegment,
      //              "World",
      //              Result.success(
      //                  baseSegment.copy(
      //                      content = "World", previousLineWrapper = ParagraphLine("World")))),
      //          CreateOrUpdateSegmentArg(
      //              null,
      //              "World",
      //              Result.failure(
      //                  InvalidSegmentException(
      //                      "No previous segment with a title to append content to. Line:
      // World"))),
      //          CreateOrUpdateSegmentArg(
      //              baseSegment.copy(title = ""),
      //              "World",
      //              Result.failure(
      //                  InvalidSegmentException(
      //                      "Previous segment has no title, cannot append content. Line:
      // World"))),
      //          CreateOrUpdateSegmentArg(
      //              baseSegment,
      //              "",
      //              Result.failure(InvalidSegmentException("Invalid line wrapper: DeleteLine.
      // Line: "))),
      //          CreateOrUpdateSegmentArg(
      //              baseSegment.copy(previousLineWrapper = TitleLine("TITLE")),
      //              "",
      //              Result.failure(InvalidSegmentException("Invalid line wrapper: NewLine. Line:
      // "))),
      //          CreateOrUpdateSegmentArg(
      //              baseSegment.copy(previousLineWrapper = TitleLine("TITLE"), content = ""),
      //              "World",
      //              Result.success(
      //                  baseSegment.copy(
      //                      content = "World", previousLineWrapper = ParagraphLine("World")))))

      return Stream.of()
    }

    @JvmStatic
    fun segmentTypeArgs(): Stream<SegmentTypeArg> =
        Stream.of(
            SegmentTypeArg(Segment("", ""), SegmentType.BLANK),
            SegmentTypeArg(Segment("HELLO", ""), SegmentType.TITLE_ONLY),
            SegmentTypeArg(Segment("HELLO", "World"), SegmentType.TITLE_AND_PARTIAL_CONTENT),
            SegmentTypeArg(Segment("HELLO", "World."), SegmentType.COMPLETE),
            SegmentTypeArg(Segment("HELLO", "World.)"), SegmentType.COMPLETE))
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

  @ParameterizedTest
  @MethodSource("segmentTypeArgs")
  fun `has correct segment type`(arg: SegmentTypeArg) {
    arg.segment.type.shouldBe(arg.type)
  }

  data class CreateOrUpdateSegmentArg(
      val previousSegment: Segment?,
      val currentLine: String,
      val expectedSegment: Result<Segment>
  )

  data class SegmentTypeArg(val segment: Segment, val type: SegmentType)
}
