package us.craigmiller160.tolkienai.server.data.migration.mongo

import com.mongodb.client.MongoDatabase
import us.craigmiller160.tolkienai.server.data.migration.Migration

interface MongoCoreMigration : Migration<MongoDatabase>
