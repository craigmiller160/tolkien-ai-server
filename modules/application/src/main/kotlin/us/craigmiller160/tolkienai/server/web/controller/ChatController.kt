package us.craigmiller160.tolkienai.server.web.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import us.craigmiller160.tolkienai.server.web.service.ChatService
import us.craigmiller160.tolkienai.server.web.type.ChatRequest
import us.craigmiller160.tolkienai.server.web.type.ChatResponse

@RestController
@RequestMapping("/chat")
class ChatController(private val chatService: ChatService) {
  @PostMapping fun chat(@RequestBody request: ChatRequest): ChatResponse = chatService.chat(request)
}
