package us.craigmiller160.tolkienai.server.ai.ingestion.service

import java.util.UUID

data class Segment(
    val title: String,
    val content: String,
    val previousLineWrapper: LineWrapper?,
    val id: UUID = UUID.randomUUID()
) {
  fun toText(): String = "$title\n$content"
}

fun createOrUpdateSegment(previousSegment: Segment?, currentLine: String): Segment {
  //  val lineWrapper = lineToLineWrapper(previousSegment?.previousLineWrapper?.line, currentLine)
  //  if (lineWrapper is NewLine || lineWrapper is DeleteLine) {
  //    throw InvalidSegmentException(
  //        "Invalid line wrapper: ${lineWrapper.javaClass.simpleName}. Line: $currentLine")
  //  }
  //
  //  if (lineWrapper is TitleLine && previousSegment?.previousLineWrapper is TitleLine) {
  //    return previousSegment.copy(
  //        title = "${previousSegment.title} ${lineWrapper.line}", previousLineWrapper =
  // lineWrapper)
  //  }
  //
  //  if (lineWrapper is TitleLine) {
  //    return Segment(title = lineWrapper.line, content = "", previousLineWrapper = lineWrapper)
  //  }
  //
  //  if (lineWrapper is ParagraphLine && previousSegment == null) {
  //    throw InvalidSegmentException(
  //        "No previous segment with a title to append content to. Line: $currentLine")
  //  }
  //
  //  if (lineWrapper is ParagraphLine && previousSegment?.title.isNullOrBlank()) {
  //    throw InvalidSegmentException(
  //        "Previous segment has no title, cannot append content. Line: $currentLine")
  //  }
  //
  //  if (lineWrapper is ParagraphLine &&
  //      previousSegment != null &&
  //      previousSegment.content.isNotBlank()) {
  //    return Segment(
  //        title = previousSegment.title,
  //        content = lineWrapper.line,
  //        previousLineWrapper = lineWrapper)
  //  }
  //
  //  if (lineWrapper is ParagraphLine && previousSegment != null) {
  //    return previousSegment.copy(content = currentLine, previousLineWrapper = lineWrapper)
  //  }
  //
  //  throw InvalidSegmentException(
  //      "Unknown set of conditions, unable to create or update segment. Line: $currentLine")
  TODO()
}
