package us.craigmiller160.tolkienai.server.config

import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import us.craigmiller160.tolkienai.server.web.type.conversions.StringToZonedDateTimeConverter
import us.craigmiller160.tolkienai.server.web.type.conversions.ZonedDateTimeToStringConverter

@Configuration
class WebConfig : WebMvcConfigurer {
  override fun addFormatters(registry: FormatterRegistry) {
    registry.addConverter(ZonedDateTimeToStringConverter())
    registry.addConverter(StringToZonedDateTimeConverter())
  }
}
