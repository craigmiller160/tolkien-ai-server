package us.craigmiller160.tolkienai.server

import java.io.File
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = ["us.craigmiller160.tolkienai.server"])
class TolkienAiApplication

private val log = LoggerFactory.getLogger(TolkienAiApplication::class.java)

fun main(args: Array<String>) {
  setupTruststore()
  runApplication<TolkienAiApplication>(*args)
}

private fun setupTruststore() {
  val truststorePath =
      TolkienAiApplication::class
          .java
          .classLoader
          .getResource("truststore.p12")!!
          .toURI()
          .also { log.debug("TrustStore Path: {}", it) }
          .let { File(it) }
          .absolutePath
  System.setProperty("javax.net.ssl.trustStore", truststorePath)
}
