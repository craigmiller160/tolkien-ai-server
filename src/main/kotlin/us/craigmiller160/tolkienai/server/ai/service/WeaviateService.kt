package us.craigmiller160.tolkienai.server.ai.service

import io.weaviate.client.WeaviateClient
import io.weaviate.client.v1.graphql.query.argument.NearVectorArgument
import io.weaviate.client.v1.graphql.query.fields.Field
import io.weaviate.client.v1.schema.model.DataType
import io.weaviate.client.v1.schema.model.Property
import io.weaviate.client.v1.schema.model.WeaviateClass
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import us.craigmiller160.tolkienai.server.ai.dto.EmbeddingSearchResult
import us.craigmiller160.tolkienai.server.ai.utils.getOrThrow

@Service
class WeaviateService(private val weaviateClient: WeaviateClient) {
  companion object {
    private const val SILMARILLION_CLASS = "silmarillion"
    private const val TEXT_FIELD = "text"
  }

  private val log = LoggerFactory.getLogger(javaClass)

  suspend fun createSilmarillionClass() =
      withContext(Dispatchers.IO) {
        log.debug("Creating silmarillion class")
        weaviateClient.schema().classDeleter().withClassName(SILMARILLION_CLASS).run().getOrThrow()

        WeaviateClass.builder()
            .className(SILMARILLION_CLASS)
            .properties(
                listOf(Property.builder().name(TEXT_FIELD).dataType(listOf(DataType.TEXT)).build()))
            .build()
            .let { weaviateClient.schema().classCreator().withClass(it).run() }
            .getOrThrow()
      }

  suspend fun searchForEmbeddings(
      queryEmbedding: List<Float>,
      limit: Int
  ): List<EmbeddingSearchResult> =
      withContext(Dispatchers.IO) {
        val graphqlResult =
            weaviateClient
                .graphQL()
                .get()
                .withFields(Field.builder().name(TEXT_FIELD).build())
                .withNearVector(
                    NearVectorArgument.builder().vector(queryEmbedding.toTypedArray()).build())
                .withLimit(limit)
                .run()
                .getOrThrow()
        println(graphqlResult.data as Map<String, Any?>) // TODO delete this
        return@withContext listOf()
      }

  suspend fun insertEmbedding(text: String, embedding: List<Float>) =
      withContext(Dispatchers.IO) {
        log.debug("Inserting embedding")
        weaviateClient
            .data()
            .creator()
            .withClassName(SILMARILLION_CLASS)
            .withID(UUID.randomUUID().toString())
            .withProperties(mapOf(TEXT_FIELD to text))
            .withVector(embedding.toTypedArray())
            .run()
            .getOrThrow()
      }
}
