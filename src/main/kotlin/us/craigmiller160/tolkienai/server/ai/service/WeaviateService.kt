package us.craigmiller160.tolkienai.server.ai.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.weaviate.client.WeaviateClient
import io.weaviate.client.v1.data.replication.model.ConsistencyLevel
import io.weaviate.client.v1.filters.Operator
import io.weaviate.client.v1.filters.WhereFilter
import io.weaviate.client.v1.graphql.query.argument.NearVectorArgument
import io.weaviate.client.v1.graphql.query.fields.Field
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import us.craigmiller160.tolkienai.server.ai.dto.EmbeddingSearchResult
import us.craigmiller160.tolkienai.server.ai.dto.EmbeddingTextMatch
import us.craigmiller160.tolkienai.server.ai.dto.GetRecordCountResult
import us.craigmiller160.tolkienai.server.ai.utils.COUNT_FIELD_NAME
import us.craigmiller160.tolkienai.server.ai.utils.CREATION_TIME_UNIX_FIELD_NAME
import us.craigmiller160.tolkienai.server.ai.utils.META_FIELD_NAME
import us.craigmiller160.tolkienai.server.ai.utils.TEXT_FIELD_NAME
import us.craigmiller160.tolkienai.server.ai.utils.dataAsMap
import us.craigmiller160.tolkienai.server.ai.utils.getOrThrow
import us.craigmiller160.tolkienai.server.config.WeaviateProperties

@Service
class WeaviateService(
    private val weaviateClient: WeaviateClient,
    private val weaviateProperties: WeaviateProperties,
    private val objectMapper: ObjectMapper
) {
  companion object {
    private const val MIN_AGE_FOR_DELETE_ALL = "1712000000000"
  }

  private val log = LoggerFactory.getLogger(javaClass)

  suspend fun searchForEmbeddings(
      queryEmbedding: List<Float>,
      limit: Int
  ): List<EmbeddingTextMatch> =
      withContext(Dispatchers.IO) {
        val graphqlResult =
            weaviateClient
                .graphQL()
                .get()
                .withClassName(weaviateProperties.className)
                .withFields(Field.builder().name(TEXT_FIELD_NAME).build())
                .withNearVector(
                    NearVectorArgument.builder().vector(queryEmbedding.toTypedArray()).build())
                .withLimit(limit)
                .run()
                .getOrThrow()
        return@withContext objectMapper
            .convertValue(graphqlResult.dataAsMap, EmbeddingSearchResult::class.java)
            .get[weaviateProperties.className]
            ?: listOf()
      }

  suspend fun insertEmbedding(text: String, embedding: List<Float>) =
      withContext(Dispatchers.IO) {
        weaviateClient
            .data()
            .creator()
            .withClassName(weaviateProperties.className)
            .withID(UUID.randomUUID().toString())
            .withProperties(mapOf(TEXT_FIELD_NAME to text))
            .withVector(embedding.toTypedArray())
            .run()
            .getOrThrow()
      }

  suspend fun getRecordCount(): Int =
      withContext(Dispatchers.IO) {
        val metaField =
            Field.builder()
                .name(META_FIELD_NAME)
                .fields(Field.builder().name(COUNT_FIELD_NAME).build())
                .build()

        val graphqlResult =
            weaviateClient
                .graphQL()
                .aggregate()
                .withClassName(weaviateProperties.className)
                .withFields(metaField)
                .run()
                .getOrThrow()

        return@withContext objectMapper
            .convertValue(graphqlResult.dataAsMap, GetRecordCountResult::class.java)
            .aggregate[weaviateProperties.className]
            ?.first()
            ?.meta
            ?.count
            ?.toInt()
            ?: 0
      }

  suspend fun deleteAllRecords(): Unit {
    val count = getRecordCount().toLong()
    log.debug("Deleting all records. Existing record count: $count")
    recursiveDeleteAllRecords(count)
  }

  private suspend fun recursiveDeleteAllRecords(recordsRemaining: Long, attemptNumber: Int = 0) {
    if (attemptNumber >= 5) {
      return
    }
    val recordsDeleted = doDeleteRecords()
    log.trace(
        "Recursively deleting all records. Attempt: $attemptNumber Remaining Records: $recordsRemaining Records Deleted: $recordsDeleted")
    val newRecordsRemaining = recordsRemaining - recordsDeleted
    if (newRecordsRemaining > 0) {
      recursiveDeleteAllRecords(newRecordsRemaining, attemptNumber + 1)
    }
  }

  private suspend fun doDeleteRecords(): Long =
      withContext(Dispatchers.IO) {
        val where =
            WhereFilter.builder()
                .operator(Operator.GreaterThanEqual)
                .path(CREATION_TIME_UNIX_FIELD_NAME)
                .valueText(MIN_AGE_FOR_DELETE_ALL)
                .build()

        weaviateClient
            .batch()
            .objectsBatchDeleter()
            .withClassName(weaviateProperties.className)
            .withConsistencyLevel(ConsistencyLevel.ALL)
            .withWhere(where)
            .run()
            .getOrThrow()
            .results
            .successful
      }
}
