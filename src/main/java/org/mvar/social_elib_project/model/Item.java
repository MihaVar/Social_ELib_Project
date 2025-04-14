package org.mvar.social_elib_project.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@Document("catalog")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Author is required")
    private String author;
    @NotBlank(message = "Description is required")
    private String description;
    @NotBlank(message = "Category is required")
    private String category;
    @NotBlank(message = "Publish date is required is required")
    private Date date;
    private String image;
    @NotBlank(message = "PDF is required")
    private String pdfLink;
    private User user;
    private ExpertComment expertComment;
}
