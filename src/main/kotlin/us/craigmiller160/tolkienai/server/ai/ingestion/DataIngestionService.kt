package us.craigmiller160.tolkienai.server.ai.ingestion

import org.bson.Document
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import us.craigmiller160.tolkienai.server.config.RawSourcesProperties

@Service
class DataIngestionService{
    private val log = LoggerFactory.getLogger(javaClass)
    @EventListener(ApplicationReadyEvent::class)
    fun ingest() {
        log.info("Ingesting data")
    }

}