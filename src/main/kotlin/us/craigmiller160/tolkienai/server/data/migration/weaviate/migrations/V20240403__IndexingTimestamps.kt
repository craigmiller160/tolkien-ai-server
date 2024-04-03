package us.craigmiller160.tolkienai.server.data.migration.weaviate.migrations

import io.weaviate.client.WeaviateClient
import io.weaviate.client.v1.misc.model.InvertedIndexConfig
import io.weaviate.client.v1.misc.model.StopwordConfig
import io.weaviate.client.v1.misc.model.VectorIndexConfig
import io.weaviate.client.v1.schema.model.DataType
import io.weaviate.client.v1.schema.model.Property
import io.weaviate.client.v1.schema.model.WeaviateClass
import us.craigmiller160.tolkienai.server.ai.utils.TEXT_FIELD_NAME
import us.craigmiller160.tolkienai.server.ai.utils.TOLKIEN_CLASS_NAME
import us.craigmiller160.tolkienai.server.ai.utils.getOrThrow
import us.craigmiller160.tolkienai.server.data.migration.weaviate.WeaviateMigration

class V20240403__IndexingTimestamps : WeaviateMigration {
  override fun migrate(helper: WeaviateClient) {
    helper.schema().classDeleter().withClassName(TOLKIEN_CLASS_NAME).run().getOrThrow()

    val indexConfig =
        VectorIndexConfig.builder()
            .ef(-1)
            .dynamicEfFactor(10)
            .dynamicEfMin(5)
            .dynamicEfMax(50)
            .build()

    val stopwordConfig = StopwordConfig.builder().preset("en").build()

    val invertedIndexConfig =
        InvertedIndexConfig.builder().stopwords(stopwordConfig).indexTimestamps(true).build()

    WeaviateClass.builder()
        .className(TOLKIEN_CLASS_NAME)
        .properties(
            listOf(
                Property.builder().name(TEXT_FIELD_NAME).dataType(listOf(DataType.TEXT)).build()))
        .vectorIndexConfig(indexConfig)
        .invertedIndexConfig(invertedIndexConfig)
        .build()
        .let { helper.schema().classCreator().withClass(it).run() }
        .getOrThrow()
  }
}
