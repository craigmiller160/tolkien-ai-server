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

data class SegmentId(val value: UUID = UUID.randomUUID()) {
  override fun toString(): String = ""
}

data class Segment(val title: String, val content: String, val id: SegmentId = SegmentId()) {
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

  return when (previousSegment?.type) {
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
    SegmentType.TITLE_AND_PARTIAL_CONTENT ->
        when (lineWrapper) {
          is TitleLine -> Segment(lineWrapper.line, "")
          else -> previousSegment.copy(content = "${previousSegment.content} ${lineWrapper.line}")
        }
    SegmentType.COMPLETE ->
        when (lineWrapper) {
          is TitleLine -> Segment(lineWrapper.line, "")
          else -> Segment(previousSegment.title, lineWrapper.line)
        }
  }
}
