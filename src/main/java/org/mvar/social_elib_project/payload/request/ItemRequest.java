package org.mvar.social_elib_project.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.mvar.social_elib_project.model.User;

import java.util.Date;

@Builder
public record ItemRequest(
        @NotBlank(message = "Name cannot be empty")
        String name,
        @NotBlank(message = "Author cannot be empty")
        String author,
        @NotBlank(message = "Description cannot be empty")
        String description,
        @NotBlank(message = "Category cannot be empty")
        String category,
        @NotBlank(message = "Date cannot be empty")
        Date date,
        @NotBlank(message = "Link to source cannot be empty")
        String pdfLink,
        @NotBlank(message = "User cannot be empty")
        String user
) {

}
