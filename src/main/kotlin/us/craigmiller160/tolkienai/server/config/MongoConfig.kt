package us.craigmiller160.tolkienai.server.config

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
      listOf(UuidToStringConverter(), StringToUuidConverter()).let { MongoCustomConversions(it) }
}

@WritingConverter
class UuidToStringConverter : Converter<UUID, String> {
  override fun convert(source: UUID): String = source.toString()
}

@ReadingConverter
class StringToUuidConverter : Converter<String, UUID> {
  override fun convert(source: String): UUID = UUID.fromString(source)
}
