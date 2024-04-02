package us.craigmiller160.tolkienai.server.web.service

import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import us.craigmiller160.tolkienai.server.ai.service.WeaviateService
import us.craigmiller160.tolkienai.server.web.type.RecordCountResponse

@Service
class DataService(private val weaviateService: WeaviateService) {
  fun getRecordCount(): RecordCountResponse = runBlocking {
    weaviateService.getRecordCount().let { RecordCountResponse(it) }
  }
}
