package us.craigmiller160.tolkienai.server.ai.service

import io.weaviate.client.WeaviateClient
import org.springframework.stereotype.Service

@Service
class WeviateService(private val weaviateClient: WeaviateClient) {
  companion object {
    private const val SILMARILLION_CLASS = "silmarillion"
  }
}
