package us.craigmiller160.tolkienai.server.ai.service

import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import org.springframework.stereotype.Service
import us.craigmiller160.tolkienai.server.ai.dto.EmbeddingContainer
import us.craigmiller160.tolkienai.server.config.OpenaiProperties

@Service
class OpenAiService(
    private val openAiClient: OpenAI,
    private val openaiProperties: OpenaiProperties
) {
  suspend fun createEmbedding(text: String): EmbeddingContainer =
      ModelId(openaiProperties.models.embedding.name)
          .let { EmbeddingRequest(model = it, input = listOf(text)) }
          .let { openAiClient.embeddings(it) }
          .let { res -> res.embeddings.flatMap { it.embedding } }
          .let {
            EmbeddingContainer(
                embedding = it,
                text = text,
                dimensions = openaiProperties.models.embedding.dimensions)
          }
}
