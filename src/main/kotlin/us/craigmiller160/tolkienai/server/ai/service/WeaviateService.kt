package us.craigmiller160.tolkienai.server.ai.service

import io.weaviate.client.WeaviateClient
import io.weaviate.client.v1.schema.model.DataType
import io.weaviate.client.v1.schema.model.Property
import io.weaviate.client.v1.schema.model.WeaviateClass
import java.util.UUID
import org.springframework.stereotype.Service
import us.craigmiller160.tolkienai.server.ai.utils.getOrThrow

@Service
class WeaviateService(private val weaviateClient: WeaviateClient) {
  companion object {
    private const val SILMARILLION_CLASS = "silmarillion"
    private const val TEXT_FIELD = "text"
  }

  fun createSilmarillionClass() {
    weaviateClient.schema().classDeleter().withClassName(SILMARILLION_CLASS).run().getOrThrow()

    WeaviateClass.builder()
        .className(SILMARILLION_CLASS)
        .properties(
            listOf(Property.builder().name(TEXT_FIELD).dataType(listOf(DataType.TEXT)).build()))
        .build()
        .let { weaviateClient.schema().classCreator().withClass(it).run() }
        .getOrThrow()
  }

  fun insertEmbedding(text: String, embedding: List<Float>) =
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
