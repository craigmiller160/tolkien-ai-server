package us.craigmiller160.tolkienai.server.config

import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date
import java.util.UUID
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.mongodb.core.convert.MongoCustomConversions

@Configuration
class MongoConfig {
  @Bean
  fun mongoCustomConversions(): MongoCustomConversions =
      listOf(
              UuidToStringConverter(),
              StringToUuidConverter(),
              ZonedDateTimeToDateConverter(),
              DateToZonedDateTimeConverter())
          .let { MongoCustomConversions(it) }
}

@WritingConverter
class UuidToStringConverter : Converter<UUID, String> {
  override fun convert(source: UUID): String = source.toString()
}

@ReadingConverter
class StringToUuidConverter : Converter<String, UUID> {
  override fun convert(source: String): UUID = UUID.fromString(source)
}

@WritingConverter
class ZonedDateTimeToDateConverter : Converter<ZonedDateTime, Date> {
  override fun convert(source: ZonedDateTime): Date =
      source.withZoneSameInstant(ZoneId.of("UTC")).toInstant().let { Date.from(it) }
}

@ReadingConverter
class DateToZonedDateTimeConverter : Converter<Date, ZonedDateTime> {
  override fun convert(source: Date): ZonedDateTime = source.toInstant().atZone(ZoneId.of("UTC"))
}
