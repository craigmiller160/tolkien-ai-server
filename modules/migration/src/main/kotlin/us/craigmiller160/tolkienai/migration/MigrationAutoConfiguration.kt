package us.craigmiller160.tolkienai.migration

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationPropertiesScan(basePackages = ["us.craigmiller160.tolkienai.migration"])
@ComponentScan(basePackages = ["us.craigmiller160.tolkienai.migration"])
class MigrationAutoConfiguration
