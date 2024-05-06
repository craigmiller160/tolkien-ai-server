package us.craigmiller160.tolkienai.server.web.type

import us.craigmiller160.tolkienai.server.ai.ingestion.DataIngestionSource

data class IngestDataRequest(val source: DataIngestionSource, val dryRun: Boolean)
