package us.craigmiller160.tolkienai.server.ai.utils

import io.weaviate.client.base.Result
import io.weaviate.client.v1.graphql.model.GraphQLResponse
import us.craigmiller160.tolkienai.server.ai.exception.toException

fun <T> Result<T>.toKotlinResult(): kotlin.Result<T> = runCatching { getOrThrow() }

fun <T> Result<T>.getOrThrow(): T {
  if (hasErrors()) {
    throw error.toException()
  }
  return result
}

val GraphQLResponse.dataAsMap
  get() = data as Map<String, Any?>

fun Result<GraphQLResponse>.toDataAsMapKotlinResult(): kotlin.Result<GraphQLResponse> =
    kotlin.runCatching { getOrThrow() }

fun Result<GraphQLResponse>.getOrThrow(): GraphQLResponse {
  if (hasErrors()) {
    throw error.toException()
  }

  if ((result.errors?.size ?: 0) > 0) {
    throw result.errors.toException()
  }
  return result
}
