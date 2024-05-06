package us.craigmiller160.tolkienai.server.data

import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.query.Query

private val log = LoggerFactory.getLogger("us.craigmiller160.tolkienai.server.data.Logger")

fun Query.log(name: String) {
  val filter = queryObject.toJson()
  val sort = sortObject.toJson()
  val fields = fieldsObject.toJson()
  log.trace(
      "MongoDB Query. Name='$name' Filter=$filter Sort=$sort Fields=$fields Limit=$limit Skip=$skip")
}
