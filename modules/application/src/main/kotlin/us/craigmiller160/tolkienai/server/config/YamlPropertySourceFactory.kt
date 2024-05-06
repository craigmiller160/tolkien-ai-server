package us.craigmiller160.tolkienai.server.config

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.env.PropertySource
import org.springframework.core.io.support.EncodedResource
import org.springframework.core.io.support.PropertySourceFactory

class YamlPropertySourceFactory : PropertySourceFactory {
  override fun createPropertySource(name: String?, resource: EncodedResource): PropertySource<*> {
    val factory = YamlPropertiesFactoryBean()
    factory.setResources(resource.resource)

    val properties = factory.getObject()

    return PropertiesPropertySource(resource.resource.filename!!, properties!!)
  }
}
