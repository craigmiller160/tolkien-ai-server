package us.craigmiller160.tolkienai.server.data.migration.mongo

import org.springframework.data.mongodb.core.MongoTemplate
import us.craigmiller160.tolkienai.server.data.migration.Migration

interface MongoSpringMigration : Migration<MongoTemplate>
