package us.craigmiller160.tolkienai.server.ai.ingestion

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import us.craigmiller160.tolkienai.server.ai.ingestion.service.Segment
import us.craigmiller160.tolkienai.server.ai.ingestion.service.TitleLine
import us.craigmiller160.tolkienai.server.ai.ingestion.service.createOrUpdateSegment
import java.util.stream.Stream

class SegmentTest {
    companion object {
        @JvmStatic
        fun createOrUpdateSegmentArgs(): Stream<CreateOrUpdateSegmentArg> {
            val baseSegment = Segment("TITLE", "Body", null)

            return Stream.of(
                CreateOrUpdateSegmentArg(null, "HELLO", Segment("HELLO", "", TitleLine("HELLO")))
            )
        }
    }
    @ParameterizedTest
    @MethodSource("createOrUpdateSegmentArgs")
    fun `creates or updates segment correctly`(arg: CreateOrUpdateSegmentArg) {
        val actualSegment = createOrUpdateSegment(arg.previousSegment, arg.currentLine)
        assertThat(actualSegment)
            .isEqualTo(arg.expectedSegment)
    }

    data class CreateOrUpdateSegmentArg(
        val previousSegment: Segment?,
        val currentLine: String,
        val expectedSegment: Segment
    )
}