package org.mvar.social_elib_project.repository;

import org.mvar.social_elib_project.model.ChatMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    default List<ChatMessage> findLast50() {
        return findAll(PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "timestamp"))).getContent();
    }

    List<ChatMessage> findTop50ByOrderByTimestampDesc();
}
