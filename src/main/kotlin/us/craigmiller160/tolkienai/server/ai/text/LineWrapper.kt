package us.craigmiller160.tolkienai.server.ai.text

sealed interface LineWrapper {
    val line: String
}

data class ParagraphLine(override val line: String): LineWrapper

data class TitleLine(override val line: String): LineWrapper

data class DeleteLine(override val line: String): LineWrapper

data class NewLine(override val line: String): LineWrapper

private val allCapsRegex = Regex("^[A-Z\\s0-9]+\$")

fun lineToLineWrapper(previousLine: String?,
                      currentLine: String): LineWrapper {
    if (allCapsRegex.matches(currentLine)) {
        return TitleLine(currentLine)
    }

    if (currentLine.isBlank() && previousLine?.isNotBlank() == true) {
        return NewLine(currentLine)
    }

    if (currentLine.isBlank() && previousLine.isNullOrBlank()) {
        return DeleteLine(currentLine)
    }

    return ParagraphLine(currentLine)
}