package us.craigmiller160.tolkienai.server.ai.utils

import io.weaviate.client.base.Result
import us.craigmiller160.tolkienai.server.ai.exception.WeaviateException

fun <T> Result<T>.toKotlinResult(): kotlin.Result<T> = runCatching { getOrThrow() }

fun <T> Result<T>.getOrThrow(): T {
  if (hasErrors()) {
    throw WeaviateException(error)
  }
  return result
}
