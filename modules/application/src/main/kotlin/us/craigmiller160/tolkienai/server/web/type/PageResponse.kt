package us.craigmiller160.tolkienai.server.web.type

interface PageResponse : PageRequest {
  val totalRecords: Long
}
