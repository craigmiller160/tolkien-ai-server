package us.craigmiller160.tolkienai.server.ai.utils

import io.weaviate.client.base.Result
import us.craigmiller160.tolkienai.server.ai.exception.toException

fun <T> Result<T>.toKotlinResult(): kotlin.Result<T> = runCatching { getOrThrow() }

fun <T> Result<T>.getOrThrow(): T {
  if (hasErrors()) {
    throw error.toException()
  }
  return result
}
