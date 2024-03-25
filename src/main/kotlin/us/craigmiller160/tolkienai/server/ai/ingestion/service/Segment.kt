package us.craigmiller160.tolkienai.server.ai.ingestion.service

import java.util.UUID
import us.craigmiller160.tolkienai.server.ai.ingestion.exception.InvalidSegmentException

enum class SegmentType {
  BLANK,
  TITLE_ONLY,
  TITLE_AND_PARTIAL_CONTENT,
  COMPLETE
}

private val CONTENT_END_REGEX = Regex("\\.\\p{Punct}*$")

data class Segment(
    val title: String,
    val content: String,
    val id: UUID = UUID.randomUUID() // TODO try and eliminate ID by filtering by COMPLETE
) {
  val type: SegmentType
    get() =
        when {
          title.isNotBlank() &&
              content.isNotBlank() &&
              CONTENT_END_REGEX.containsMatchIn(content) -> SegmentType.COMPLETE
          title.isNotBlank() && content.isNotBlank() -> SegmentType.TITLE_AND_PARTIAL_CONTENT
          title.isNotBlank() && content.isBlank() -> SegmentType.TITLE_ONLY
          else -> SegmentType.BLANK
        }
  fun toText(): String = "$title\n$content"
}

fun createOrUpdateSegment(previousSegment: Segment?, currentLine: String): Segment {
  val lineWrapper = lineToLineWrapper(null, currentLine)
  if (lineWrapper is NewLine || lineWrapper is DeleteLine) {
    throw InvalidSegmentException(
        "Invalid line wrapper: ${lineWrapper.javaClass.simpleName}. Line: $currentLine")
  }

  when (previousSegment?.type) {
    null,
    SegmentType.BLANK ->
        when (lineWrapper) {
          is TitleLine -> Segment(lineWrapper.line, "")
          else ->
              throw InvalidSegmentException(
                  "No previous segment with a title to append content to. Line: $currentLine")
        }
    SegmentType.TITLE_ONLY ->
        when (lineWrapper) {
          is TitleLine ->
              previousSegment.copy(title = "${previousSegment.title} ${lineWrapper.line}")
          else -> previousSegment.copy(content = lineWrapper.line)
        }
    else -> TODO()
  }

  if (lineWrapper is TitleLine && previousSegment?.type == SegmentType.TITLE_ONLY) {
    return previousSegment.copy(title = "${previousSegment.title} ${lineWrapper.line}")
  }

  if (lineWrapper is TitleLine && previousSegment?.type == SegmentType.BLANK) {}

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
  throw InvalidSegmentException(
      "No previous segment with a title to append content to. Line: $currentLine")
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
