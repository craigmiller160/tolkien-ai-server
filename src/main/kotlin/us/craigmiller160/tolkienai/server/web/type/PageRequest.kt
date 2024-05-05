package us.craigmiller160.tolkienai.server.web.type

import org.springframework.data.domain.Pageable

interface PageRequest {
  val pageNumber: Int
  val pageSize: Int
}

val PageRequest.page: Pageable
  get() = org.springframework.data.domain.PageRequest.of(pageNumber, pageSize)
