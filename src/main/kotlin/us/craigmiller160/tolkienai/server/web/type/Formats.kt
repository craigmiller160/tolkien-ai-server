package us.craigmiller160.tolkienai.server.web.type

import java.time.format.DateTimeFormatter

const val TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
val TIMESTAMP_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT)
