package us.craigmiller160.tolkienai.server.testcore

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import us.craigmiller160.testcontainers.common.TestcontainersExtension
import us.craigmiller160.tolkienai.server.config.YamlPropertySourceFactory

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@SpringBootTest
@ExtendWith(value = [TestcontainersExtension::class, SpringExtension::class, MockExtension::class])
@AutoConfigureMockMvc
@ActiveProfiles("test")
@PropertySources(
    value =
        [
            PropertySource(
                value = ["classpath:application.yml"], factory = YamlPropertySourceFactory::class),
            PropertySource(
                value = ["classpath:application-dev.yml"],
                factory = YamlPropertySourceFactory::class),
            PropertySource(
                value = ["classpath:application-test.yml"],
                factory = YamlPropertySourceFactory::class)])
annotation class IntegrationTest
