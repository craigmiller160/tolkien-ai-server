package us.craigmiller160.tolkienai.server.ai.ingestion

import io.kotest.matchers.shouldBe
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import us.craigmiller160.tolkienai.server.ai.ingestion.service.DeleteLine
import us.craigmiller160.tolkienai.server.ai.ingestion.service.LineWrapper
import us.craigmiller160.tolkienai.server.ai.ingestion.service.NewLine
import us.craigmiller160.tolkienai.server.ai.ingestion.service.ParagraphLine
import us.craigmiller160.tolkienai.server.ai.ingestion.service.TitleLine
import us.craigmiller160.tolkienai.server.ai.ingestion.service.lineToLineWrapper

class LineWrapperTest {
  companion object {
    @JvmStatic
    fun lineToLineWrapperArgs(): Stream<LineToLineWrapperArg> {
      return Stream.of(
          LineToLineWrapperArg(null, "Hello", ParagraphLine("Hello")),
          LineToLineWrapperArg("THE TITLE", "Hello", ParagraphLine("Hello")),
          LineToLineWrapperArg("World", "Hello", ParagraphLine("Hello")),
          LineToLineWrapperArg("Hello", "", NewLine("")),
          LineToLineWrapperArg("THE TITLE", "", NewLine("")),
          LineToLineWrapperArg("", "", DeleteLine("")),
          LineToLineWrapperArg(null, "THE TITLE", TitleLine("THE TITLE")),
          LineToLineWrapperArg("OTHER TITLE", "THE TITLE", TitleLine("THE TITLE")),
          LineToLineWrapperArg("Hello", "THE TITLE", TitleLine("THE TITLE")),
          LineToLineWrapperArg(null, "", DeleteLine("")),
          LineToLineWrapperArg(null, "J.R.R", TitleLine("J.R.R")))
    }
  }
  @ParameterizedTest
  @MethodSource("lineToLineWrapperArgs")
  fun `converts lines to LineWrappers correctly`(arg: LineToLineWrapperArg) {
    val actualWrapper = lineToLineWrapper(arg.previousLine, arg.currentLine)
    actualWrapper.shouldBe(arg.expectedWrapper)
  }

  data class LineToLineWrapperArg(
      val previousLine: String?,
      val currentLine: String,
      val expectedWrapper: LineWrapper
  )
}
