package org.mvar.social_elib_project.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("messages")
public class ChatMessage {
    @Id
    private String id;
    private String content;
    private String sender;
    private LocalDateTime timestamp;
}
