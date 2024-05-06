package us.craigmiller160.tolkienai.server.config

import org.springframework.boot.context.properties.ConfigurationProperties

data class ChatQueryProperties(val recordLimit: Int)

@ConfigurationProperties(prefix = "tolkienai.chat")
data class ChatProperties(val query: ChatQueryProperties)
