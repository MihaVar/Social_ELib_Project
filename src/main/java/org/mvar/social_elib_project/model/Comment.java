package org.mvar.social_elib_project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Builder
@Document("comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    @NotBlank(message = "Comment text cannot be empty")
    private String text;
    @NotBlank(message = "Comment date cannot be empty")
    private Date date;
    @NotBlank(message = "Item cannot be empty")
    private String itemId;
    @NotBlank(message = "User cannot be empty")
    private String user;
}
