package org.mvar.social_elib_project.payload.request.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AdminDeleteCommentRequest(
        @NotBlank(message = "Comment id should not be empty")
        String commentId
) {
}
