package us.craigmiller160.tolkienai.server.data.migration

import com.mongodb.client.MongoClient
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component

@Component
class MigrationRunner(private val client: MongoClient, private val mongoTemplate: MongoTemplate) {}
