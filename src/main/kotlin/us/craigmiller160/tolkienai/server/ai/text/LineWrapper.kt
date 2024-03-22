package us.craigmiller160.tolkienai.server.ai.text

sealed interface LineWrapper {
    val line: String
}

data class ParagraphLine(override val line: String): LineWrapper

data class TitleLine(override val line: String): LineWrapper

data class DeleteLine(override val line: String): LineWrapper

fun lineToLineWrapper(previousLine: String?,
                      currentLine: String): LineWrapper {

}