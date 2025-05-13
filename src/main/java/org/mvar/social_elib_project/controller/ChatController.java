package org.mvar.social_elib_project.controller;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.repository.ChatMessageRepository;
import org.mvar.social_elib_project.service.ChatService;
import org.mvar.social_elib_project.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatMessageRepository chatMessageRepository;

    @MessageMapping("/chat.send")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message) {
        return chatService.sendMessage(message);
    }

    @GetMapping("/api/chat/last-messages")
    public List<ChatMessage> getLastMessages() {
        return chatMessageRepository.findTop50ByOrderByTimestampDesc();
    }

    @GetMapping("api/chat/messages")
    public List<ChatMessage> getMessages() {
        return chatService.getChatMessages();
    }
}
