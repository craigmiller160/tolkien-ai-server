package us.craigmiller160.tolkienai.server.web.type.conversions

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.time.ZoneId
import java.time.ZonedDateTime
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import us.craigmiller160.tolkienai.server.web.type.TIMESTAMP_FORMATTER

@ReadingConverter
class ZonedDateTimeToStringConverter : Converter<ZonedDateTime, String> {
  override fun convert(dateTime: ZonedDateTime): String =
      dateTime.withZoneSameInstant(ZoneId.of("UTC")).format(TIMESTAMP_FORMATTER)
}

@WritingConverter
class StringToZonedDateTimeConverter : Converter<String, ZonedDateTime> {
  override fun convert(source: String): ZonedDateTime =
      ZonedDateTime.parse(source, TIMESTAMP_FORMATTER.withZone(ZoneId.of("UTC")))
}

class ZonedDateTimeSerializer : JsonSerializer<ZonedDateTime>() {
  private val converter = ZonedDateTimeToStringConverter()
  override fun serialize(
      zonedDateTime: ZonedDateTime,
      generator: JsonGenerator,
      provider: SerializerProvider
  ) = converter.convert(zonedDateTime).let { generator.writeString(it) }
}

class ZonedDateTimeDeserializer : JsonDeserializer<ZonedDateTime>() {
  private val converter = StringToZonedDateTimeConverter()
  override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): ZonedDateTime =
      converter.convert(parser.text)
}
