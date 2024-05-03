package us.craigmiller160.tolkienai.server.testcore

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import us.craigmiller160.testcontainers.common.spring.SpringTestContainersExtension

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@SpringBootTest
@ExtendWith(
    value = [SpringTestContainersExtension::class, SpringExtension::class, MockExtension::class])
@AutoConfigureMockMvc
@ActiveProfiles("test")
annotation class IntegrationTest
