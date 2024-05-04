package us.craigmiller160.tolkienai.server.config

import java.time.ZoneId
import java.time.ZonedDateTime
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import us.craigmiller160.tolkienai.server.web.type.TIMESTAMP_FORMATTER

@Configuration
class WebConfig : WebMvcConfigurer {
  override fun addFormatters(registry: FormatterRegistry) {
    registry.addConverter(ZonedDateTimeToStringConverter())
    registry.addConverter(StringToZonedDateTimeConverter())
  }
}

@ReadingConverter
class ZonedDateTimeToStringConverter : Converter<ZonedDateTime, String> {
  override fun convert(dateTime: ZonedDateTime): String =
      dateTime.withZoneSameInstant(ZoneId.of("UTC")).format(TIMESTAMP_FORMATTER)
}

@WritingConverter
class StringToZonedDateTimeConverter : Converter<String, ZonedDateTime> {
  override fun convert(source: String): ZonedDateTime =
      ZonedDateTime.parse(source, TIMESTAMP_FORMATTER, ZoneId.of("UTC"))
}
