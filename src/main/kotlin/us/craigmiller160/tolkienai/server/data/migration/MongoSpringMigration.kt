package us.craigmiller160.tolkienai.server.data.migration

import org.springframework.data.mongodb.core.MongoTemplate

interface MongoSpringMigration : Migration<MongoTemplate>
