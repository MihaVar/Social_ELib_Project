package org.mvar.social_elib_project.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("tokens")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    @Id
    @GeneratedValue
    private String id;
    @NotBlank(message = "JWT token is required")
    @Indexed(unique = true)
    private String jwt;
    @Setter
    private boolean isValid;
    @NotNull
    private Date expireDate;
}
