package org.mvar.social_elib_project.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("expertcomments")
public class ExpertComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @NotBlank(message = "Expert comment id is required")
    private long expertCommentId;
    @NotBlank(message = "Text is required")
    private String text;
    @CreatedDate
    private LocalDateTime creationDate;
    @NotBlank(message = "Item is required")
    private long itemId;
    @NotBlank(message = "User is required")
    private String user;
}
