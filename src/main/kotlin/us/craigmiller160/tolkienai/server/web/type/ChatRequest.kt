package us.craigmiller160.tolkienai.server.web.type

data class ChatRequest(val query: String, val group: String? = null)
