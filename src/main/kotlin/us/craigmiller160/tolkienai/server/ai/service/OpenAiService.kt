package us.craigmiller160.tolkienai.server.ai.service

import com.aallam.openai.client.OpenAI
import us.craigmiller160.tolkienai.server.config.OpenaiProperties

class OpenAiService(
    private val openAiClient: OpenAI,
    private val openaiProperties: OpenaiProperties
) {}
