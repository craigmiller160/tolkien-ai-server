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

      return Stream.of(
          CreateOrUpdateSegmentArg(null, "HELLO", Result.success(Segment("HELLO", ""))),
          CreateOrUpdateSegmentArg(
              Segment("HELLO", ""), "WORLD", Result.success(Segment("HELLO WORLD", ""))),
          CreateOrUpdateSegmentArg(
              Segment("HELLO", ""), "World", Result.success(Segment("HELLO", "World"))),
          CreateOrUpdateSegmentArg(
              Segment("HELLO", "World"),
              "Universe",
              Result.success(Segment("HELLO", "World Universe"))),
          CreateOrUpdateSegmentArg(
              Segment("HELLO", "World."), "UNIVERSE", Result.success(Segment("UNIVERSE", ""))),
          CreateOrUpdateSegmentArg(
              Segment("HELLO", "World."), "Universe", Result.success(Segment("HELLO", "Universe"))),
          CreateOrUpdateSegmentArg(
              Segment("HELLO", "World"),
              "",
              Result.failure(InvalidSegmentException("Invalid line wrapper: DeleteLine. Line: "))),
          CreateOrUpdateSegmentArg(
              null,
              "Universe",
              Result.failure(
                  InvalidSegmentException(
                      "No previous segment with a title to append content to. Line: Universe"))),
          CreateOrUpdateSegmentArg(
              Segment("", ""),
              "Universe",
              Result.failure(
                  InvalidSegmentException(
                      "Previous segment has no title, cannot append content. Line: Universe")),
          ),
          CreateOrUpdateSegmentArg(
              Segment("HELLO", "World"),
              "UNIVERSE",
              Result.failure(
                  InvalidSegmentException(
                      "Previous segment does not have completed content, new title is not allowed. Line: UNIVERSE"))))
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
