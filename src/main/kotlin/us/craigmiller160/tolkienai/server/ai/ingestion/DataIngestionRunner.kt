package us.craigmiller160.tolkienai.server.ai.ingestion

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import us.craigmiller160.tolkienai.server.ai.ingestion.mongo.IngestionMetadataRepository
import us.craigmiller160.tolkienai.server.ai.ingestion.mongo.IngestionStatus
import us.craigmiller160.tolkienai.server.ai.ingestion.service.RawSourceParsingService

@Component
class DataIngestionRunner (
    private val ingestionMetadataRepository: IngestionMetadataRepository,
    private val rawSourceParsingService: RawSourceParsingService
){
    private val log = LoggerFactory.getLogger(javaClass)
    @EventListener(ApplicationReadyEvent::class)
    fun ingest() {
        log.info("Getting ingestion metadata")
        val metadata = ingestionMetadataRepository.getIngestionMetadata()
        if (metadata.silmarillionStatus == IngestionStatus.PENDING) {
            log.info("Performing ingestion of Silmarillion")
            rawSourceParsingService.parseSilmarillion()
        } else {
            log.info("Silmarillion ingestion already completed")
        }
    }

}