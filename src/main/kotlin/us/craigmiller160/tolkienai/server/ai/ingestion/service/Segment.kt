package us.craigmiller160.tolkienai.server.ai.ingestion.service

import us.craigmiller160.tolkienai.server.ai.ingestion.exception.InvalidSegmentException

data class Segment(val title: String, val content: String, val previousLineWrapper: LineWrapper?)

fun createOrUpdateSegment(previousSegment: Segment?, currentLine: String): Segment {
  val lineWrapper = lineToLineWrapper(previousSegment?.previousLineWrapper?.line, currentLine)
  if (lineWrapper is NewLine || lineWrapper is DeleteLine) {
    throw InvalidSegmentException("Invalid line wrapper: ${lineWrapper.javaClass.simpleName}")
  }
  if (lineWrapper is TitleLine && previousSegment?.previousLineWrapper is TitleLine) {
    return previousSegment.copy(
        title = "${previousSegment.title} ${lineWrapper.line}", previousLineWrapper = lineWrapper)
  }
  return previousSegment!!
}
