package us.craigmiller160.tolkienai.server.web.type

import org.springframework.data.domain.Pageable

interface PageRequest {
  val pageNumber: Int
  val pageSize: Int
}

fun PageRequest.toPage(): Pageable =
    org.springframework.data.domain.PageRequest.of(pageNumber, pageSize)
