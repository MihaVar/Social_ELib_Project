package org.mvar.social_elib_project.model;

import jakarta.persistence.*;
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
@Document("comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    @NotBlank(message = "Comment text cannot be empty")
    private long commentId;
    @NotBlank(message = "Comment text cannot be empty")
    private String text;
    @CreatedDate
    private LocalDateTime date;
    @NotBlank(message = "Item cannot be empty")
    private long itemId;
    @NotBlank(message = "User cannot be empty")
    private String user;
}
