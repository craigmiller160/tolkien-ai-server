package us.craigmiller160.tolkienai.migration.mongo

import com.mongodb.client.MongoDatabase
import org.springframework.data.mongodb.core.MongoTemplate

data class MongoMigrationHelper(val database: MongoDatabase, val template: MongoTemplate)
