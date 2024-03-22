package us.craigmiller160.tolkienai.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = ["us.craigmiller160.tolkienai.server"])
class TolkienAiApplication

fun main(args: Array<String>) {
    runApplication<TolkienAiApplication>(*args)
}
