package us.craigmiller160.tolkienai.migration

import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.type.classreading.CachingMetadataReaderFactory

fun <T> loadMigrations(vararg packagePaths: String): List<Migration<T>> {
  val resolver = PathMatchingResourcePatternResolver()
  val factory = CachingMetadataReaderFactory(resolver)
  return packagePaths
      .asSequence()
      .flatMap { path -> resolver.getResources("$path/*.class").toList() }
      .map { resource -> factory.getMetadataReader(resource).classMetadata.className }
      .map { Class.forName(it) }
      .filter { clazz -> Migration::class.java.isAssignableFrom(clazz) }
      .map { clazz -> clazz.getDeclaredConstructor().newInstance() }
      .map { obj -> obj as Migration<T> }
      .sortedBy { it.javaClass.simpleName }
      .toList()
}
