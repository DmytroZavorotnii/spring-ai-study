package zavorotnii.dmytro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import zavorotnii.dmytro.service.ChatService;

@RestController
@RequestMapping
public class StreamingChatController {
    @Autowired
    private ChatService chatService;

    @GetMapping(value = "/chat-stream/{chatId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter talkToModel(@PathVariable String chatId, @RequestParam String userPrompt){
        return chatService.proceedInteractionWithStreaming(chatId, userPrompt);
    }
}
