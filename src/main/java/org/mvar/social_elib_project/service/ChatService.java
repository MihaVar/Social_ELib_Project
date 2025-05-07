package org.mvar.social_elib_project.service;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.ChatMessage;
import org.mvar.social_elib_project.repository.ChatMessageRepository;
import org.mvar.social_elib_project.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ChatMessage sendMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getChatMessages() {
        return chatMessageRepository.findAll();
    }
}
