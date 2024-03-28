package us.craigmiller160.tolkienai.server.ai.exception

import io.weaviate.client.base.WeaviateError

class WeaviateException(error: WeaviateError) :
    RuntimeException(error.messages.joinToString(",") { it.message })

fun WeaviateError.toException(): WeaviateException = WeaviateException(this)
