package us.craigmiller160.tolkienai.server.data.migration.weaviate

import io.weaviate.client.WeaviateClient
import us.craigmiller160.tolkienai.server.data.migration.Migration

interface WeaviateMigration : Migration<WeaviateClient>
