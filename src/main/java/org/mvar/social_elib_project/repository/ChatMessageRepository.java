package org.mvar.social_elib_project.repository;

import org.mvar.social_elib_project.model.ChatMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findTop50ByOrderByTimestampDesc();
}
