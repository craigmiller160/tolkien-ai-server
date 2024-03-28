package us.craigmiller160.tolkienai.server.ai.service

import io.weaviate.client.WeaviateClient
import io.weaviate.client.v1.schema.model.DataType
import io.weaviate.client.v1.schema.model.Property
import io.weaviate.client.v1.schema.model.WeaviateClass
import org.springframework.stereotype.Service
import us.craigmiller160.tolkienai.server.ai.utils.getOrThrow

@Service
class WeviateService(private val weaviateClient: WeaviateClient) {
  companion object {
    private const val SILMARILLION_CLASS = "silmarillion"
    private const val TEXT_FIELD = "text"
  }

  fun createSilmarillionClass() {
    WeaviateClass.builder()
        .className(SILMARILLION_CLASS)
        .properties(
            listOf(Property.builder().name(TEXT_FIELD).dataType(listOf(DataType.TEXT)).build()))
        .build()
        .let { weaviateClient.schema().classCreator().withClass(it).run() }
        .getOrThrow()
  }
}
