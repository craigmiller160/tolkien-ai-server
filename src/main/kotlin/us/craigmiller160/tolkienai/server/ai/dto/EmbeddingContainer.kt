package us.craigmiller160.tolkienai.server.ai.dto

data class EmbeddingContainer(val embedding: List<Double>, val text: String, val dimensions: Int)

val EmbeddingContainer.floatEmbedding
  get() = embedding.map { it.toFloat() }
