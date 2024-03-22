package us.craigmiller160.tolkienai.server.ai.ingestion.service

data class Segment(val title: String, val content: String, val previousLineWrapper: LineWrapper?)

fun createOrUpdateSegment(previousSegment: Segment?, currentLine: String): Segment {
  val lineWrapper = lineToLineWrapper(null, currentLine)

  if (lineWrapper is TitleLine && previousSegment?.previousLineWrapper is TitleLine) {
    return previousSegment.copy(
        title = "${previousSegment.title} ${lineWrapper.line}", previousLineWrapper = lineWrapper)
  }
  return previousSegment!!
}
