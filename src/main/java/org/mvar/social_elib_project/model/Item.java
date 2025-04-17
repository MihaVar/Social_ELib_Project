package org.mvar.social_elib_project.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@Document("catalog")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
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
    private String pdfLink;
    @NotBlank(message = "User is required")
    private String user;
    private ExpertComment expertComment;
    private int rating = 0;
    private Set<String> usersWhoVoted = new HashSet<>();
}
