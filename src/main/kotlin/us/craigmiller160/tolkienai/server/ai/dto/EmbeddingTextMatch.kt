package us.craigmiller160.tolkienai.server.ai.dto

data class EmbeddingTextMatch(val text: String)

data class EmbeddingSearchGraphqlResult(val Get: Map<String, List<EmbeddingTextMatch>>)
