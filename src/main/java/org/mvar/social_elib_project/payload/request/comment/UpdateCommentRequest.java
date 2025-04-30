package org.mvar.social_elib_project.payload.request.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateCommentRequest(
        @NotBlank(message = "Text cannot be empty")
        String text
) {
}
