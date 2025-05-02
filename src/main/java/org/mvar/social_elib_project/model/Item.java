package org.mvar.social_elib_project.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("catalog")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @NotBlank(message = "Item id is required")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "sequences", sequenceName = "items_sequence")
    private long itemId;
    @NotBlank(message = "Name is required")
    @Getter
    private String name;
    @NotBlank(message = "Author is required")
    private String author;
    @NotBlank(message = "Description is required")
    private String description;
    @NotBlank(message = "Category is required")
    private String category;
    @NotBlank(message = "Publish date is required")
    private String publishDate;
    @CreatedDate
    private LocalDateTime creationDate;
    private String image;
    @NotBlank(message = "PDF is required")
    private String materialLink;
    @NotBlank(message = "User is required")
    private String user;
    private Set<ExpertComment> expertComment = new HashSet<>();
    private int rating = 0;
    private Set<String> usersWhoVoted = new HashSet<>();
}
