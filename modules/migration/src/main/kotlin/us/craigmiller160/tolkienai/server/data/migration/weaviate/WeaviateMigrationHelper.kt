package us.craigmiller160.tolkienai.server.data.migration.weaviate

import io.weaviate.client.WeaviateClient

data class WeaviateMigrationHelper(val client: WeaviateClient, val className: String)
