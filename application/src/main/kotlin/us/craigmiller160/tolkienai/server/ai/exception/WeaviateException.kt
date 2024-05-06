package us.craigmiller160.tolkienai.server.ai.exception

import io.weaviate.client.base.WeaviateError
import io.weaviate.client.v1.graphql.model.GraphQLError

class WeaviateException(message: String) : RuntimeException(message)

fun WeaviateError.toException(): WeaviateException =
    messages.joinToString(",") { it.message }.let { WeaviateException(it) }

fun Array<GraphQLError>.toException(): WeaviateException =
    joinToString(",") { it.message }.let { WeaviateException(it) }
