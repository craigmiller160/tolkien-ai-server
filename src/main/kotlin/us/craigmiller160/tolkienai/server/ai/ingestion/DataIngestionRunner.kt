package us.craigmiller160.tolkienai.server.ai.ingestion

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Component
class DataIngestionRunner{
    private val log = LoggerFactory.getLogger(javaClass)
    @EventListener(ApplicationReadyEvent::class)
    fun ingest() {
        log.info("Ingesting data")
    }

}