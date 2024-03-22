package us.craigmiller160.tolkienai.server.ai.ingestion.mongo

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class IngestionMetadataRepository(private val mongoTemplate: MongoTemplate) {
  fun getIngestionMetadata(): IngestionMetadata =
      mongoTemplate.findAll(IngestionMetadata::class.java).firstOrNull()
          ?: IngestionMetadata("", IngestionStatus.PENDING)

  fun updateIngestionMetadata(metadata: IngestionMetadata): IngestionMetadata {
    if (metadata.id.isBlank()) {
      return mongoTemplate.insert(metadata)
    }

    return Query()
        .addCriteria(Criteria.where("_id").`is`(metadata.id))
        .let { query -> mongoTemplate.replace(query, metadata) }
        .let { metadata }
  }
}
