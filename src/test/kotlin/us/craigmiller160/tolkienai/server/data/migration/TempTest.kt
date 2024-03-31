package us.craigmiller160.tolkienai.server.data.migration

import org.junit.jupiter.api.Test
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.type.classreading.CachingMetadataReaderFactory

/** TODO keeping this as an example of how to scan the classpath for stuff */
class TempTest {
  @Test
  fun experiment() {
    val resolver = PathMatchingResourcePatternResolver()
    val factory = CachingMetadataReaderFactory(resolver)
    resolver
        .getResources(
            "classpath:us/craigmiller160/tolkienai/server/data/migration/test_migrations/*.class")
        .map { resource -> factory.getMetadataReader(resource).classMetadata.className }
        .forEach { println(it) }
  }
}
