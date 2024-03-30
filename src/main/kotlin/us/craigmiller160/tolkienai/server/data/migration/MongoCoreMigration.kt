package us.craigmiller160.tolkienai.server.data.migration

import com.mongodb.client.MongoDatabase

interface MongoCoreMigration : Migration<MongoDatabase>
