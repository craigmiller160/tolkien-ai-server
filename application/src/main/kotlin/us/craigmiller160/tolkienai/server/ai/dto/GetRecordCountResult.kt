package us.craigmiller160.tolkienai.server.ai.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GetRecordCountMeta(val count: Double)

data class GetRecordCountEntry(val meta: GetRecordCountMeta)

data class GetRecordCountResult(
    @field:JsonProperty("Aggregate") val aggregate: Map<String, List<GetRecordCountEntry>>
)
