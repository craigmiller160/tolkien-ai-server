package us.craigmiller160.tolkienai.server.ai.text

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class LineWrapperTest {
    companion object {
        @JvmStatic
        fun lineToLineWrapperArgs(): Stream<LineToLineWrapperArg> {
            return Stream.of()
        }
    }
    @ParameterizedTest
    @MethodSource("lineToLineWrapperArgs")
    fun `converts lines to LineWrappers correctly`(arg: LineToLineWrapperArg) {
        val actualWrapper = lineToLineWrapper(arg.previousLine, arg.currentLine)
        assertThat(actualWrapper)
            .isEqualTo(arg.expectedWrapper)
    }

    data class LineToLineWrapperArg(
        val previousLine: String?,
        val currentLine: String,
        val expectedWrapper: LineWrapper
    )
}