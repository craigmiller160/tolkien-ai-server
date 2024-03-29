package us.craigmiller160.tolkienai.server.ai.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class EmbeddingTextMatch(val text: String)

data class EmbeddingSearchGraphqlResult(
    @field:JsonProperty("Get") val get: Map<String, List<EmbeddingTextMatch>>
)
