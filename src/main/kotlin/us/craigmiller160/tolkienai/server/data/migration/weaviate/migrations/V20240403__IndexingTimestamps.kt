package us.craigmiller160.tolkienai.server.data.migration.weaviate.migrations

import io.weaviate.client.v1.misc.model.InvertedIndexConfig
import io.weaviate.client.v1.misc.model.StopwordConfig
import io.weaviate.client.v1.misc.model.VectorIndexConfig
import io.weaviate.client.v1.schema.model.DataType
import io.weaviate.client.v1.schema.model.Property
import io.weaviate.client.v1.schema.model.WeaviateClass
import us.craigmiller160.tolkienai.server.ai.utils.TEXT_FIELD_NAME
import us.craigmiller160.tolkienai.server.ai.utils.getOrThrow
import us.craigmiller160.tolkienai.server.data.migration.weaviate.WeaviateMigration
import us.craigmiller160.tolkienai.server.data.migration.weaviate.WeaviateMigrationHelper

class V20240403__IndexingTimestamps : WeaviateMigration {
  override fun migrate(helper: WeaviateMigrationHelper) {
    helper.client.schema().classDeleter().withClassName(helper.className).run().getOrThrow()

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
        .className(helper.className)
        .properties(
            listOf(
                Property.builder().name(TEXT_FIELD_NAME).dataType(listOf(DataType.TEXT)).build()))
        .vectorIndexConfig(indexConfig)
        .invertedIndexConfig(invertedIndexConfig)
        .build()
        .let { helper.client.schema().classCreator().withClass(it).run() }
        .getOrThrow()
  }
}
