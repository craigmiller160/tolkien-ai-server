package us.craigmiller160.tolkienai.server.ai.ingestion

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class SegmentTest {
    companion object {
        @JvmStatic
        fun createOrUpdateSegmentArgs(): Stream<CreateOrUpdateSegmentArg> {
            TODO()
        }
    }
    @ParameterizedTest
    @MethodSource("createOrUpdateSegmentArgs")
    fun `creates or updates segment correctly`() {
        TODO()
    }

    data class CreateOrUpdateSegmentArg(
        val line: String
    )
}