package us.craigmiller160.tolkienai.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import java.io.File

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = ["us.craigmiller160.tolkienai.server"])
class TolkienAiApplication

fun main(args: Array<String>) {
    setupTruststore()
    runApplication<TolkienAiApplication>(*args)
}

private fun setupTruststore() {
    val truststorePath = TolkienAiApplication::class.java.classLoader.getResource("truststore.p12")!!
        .toURI()
        .let { File(it) }
        .absolutePath
    System.setProperty("javax.net.ssl.trustStore", truststorePath)
}