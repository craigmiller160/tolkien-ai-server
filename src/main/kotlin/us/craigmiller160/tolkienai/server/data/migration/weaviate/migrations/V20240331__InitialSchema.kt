package us.craigmiller160.tolkienai.server.data.migration.weaviate.migrations

import io.weaviate.client.WeaviateClient
import io.weaviate.client.v1.misc.model.VectorIndexConfig
import io.weaviate.client.v1.schema.model.DataType
import io.weaviate.client.v1.schema.model.Property
import io.weaviate.client.v1.schema.model.WeaviateClass
import us.craigmiller160.tolkienai.server.ai.utils.getOrThrow
import us.craigmiller160.tolkienai.server.data.migration.weaviate.WeaviateMigration

class V20240331__InitialSchema : WeaviateMigration {
  override fun migrate(helper: WeaviateClient) {
    val indexConfig =
        VectorIndexConfig.builder()
            .ef(-1)
            .dynamicEfFactor(10)
            .dynamicEfMin(5)
            .dynamicEfMax(50)
            .build()

    WeaviateClass.builder()
        .className(TOLKIEN_CLASS_NAME)
        .properties(
            listOf(
                Property.builder().name(TEXT_FIELD_NAME).dataType(listOf(DataType.TEXT)).build()))
        .vectorIndexConfig(indexConfig)
        .build()
        .let { helper.schema().classCreator().withClass(it).run() }
        .getOrThrow()
  }
}
