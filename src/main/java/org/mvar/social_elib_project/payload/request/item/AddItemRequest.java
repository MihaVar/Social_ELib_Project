package org.mvar.social_elib_project.payload.request.item;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record AddItemRequest(
        @NotBlank(message = "Name cannot be empty")
        String name,
        @NotBlank(message = "Author cannot be empty")
        String author,
        @NotBlank(message = "Description cannot be empty")
        String description,
        @NotBlank(message = "Category cannot be empty")
        String category,
        @NotBlank(message = "Date cannot be empty")
        String publishDate,
        @NotBlank(message = "Link to source cannot be empty")
        String materialLink,
        MultipartFile image
) {

}
