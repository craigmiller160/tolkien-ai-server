package us.craigmiller160.tolkienai.server.ai.ingestion.service

import java.util.UUID
import us.craigmiller160.tolkienai.server.ai.ingestion.exception.InvalidSegmentException

data class Segment(
    val title: String,
    val content: String,
    val previousLineWrapper: LineWrapper?,
    val id: UUID = UUID.randomUUID()
)

fun createOrUpdateSegment(previousSegment: Segment?, currentLine: String): Segment {
  val lineWrapper = lineToLineWrapper(previousSegment?.previousLineWrapper?.line, currentLine)
  if (lineWrapper is NewLine || lineWrapper is DeleteLine) {
    throw InvalidSegmentException("Invalid line wrapper: ${lineWrapper.javaClass.simpleName}")
  }

  if (lineWrapper is TitleLine && previousSegment?.previousLineWrapper is TitleLine) {
    return previousSegment.copy(
        title = "${previousSegment.title} ${lineWrapper.line}", previousLineWrapper = lineWrapper)
  }

  if (lineWrapper is TitleLine) {
    return Segment(title = lineWrapper.line, content = "", previousLineWrapper = lineWrapper)
  }

  if (lineWrapper is ParagraphLine && previousSegment == null) {
    throw InvalidSegmentException("No previous segment with a title to append content to")
  }

  if (lineWrapper is ParagraphLine && previousSegment != null) {
    return Segment(
        title = previousSegment.title,
        content = lineWrapper.line,
        previousLineWrapper = lineWrapper)
  }

  throw InvalidSegmentException("Unknown set of conditions, unable to create or update segment")
}
