package us.craigmiller160.tolkienai.server.config

import com.fasterxml.jackson.databind.module.SimpleModule
import java.time.ZonedDateTime
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import us.craigmiller160.tolkienai.server.web.type.conversions.ZonedDateTimeDeserializer
import us.craigmiller160.tolkienai.server.web.type.conversions.ZonedDateTimeSerializer

@Configuration
class JacksonConfig {

  private val zonedDateTimeModule =
      SimpleModule().apply {
        addSerializer(ZonedDateTime::class.java, ZonedDateTimeSerializer())
        addDeserializer(ZonedDateTime::class.java, ZonedDateTimeDeserializer())
      }

  @Bean
  fun objectMapperCustomizer(): Jackson2ObjectMapperBuilderCustomizer =
      Jackson2ObjectMapperBuilderCustomizer { builder ->
        builder.modules(zonedDateTimeModule)
      }
}
