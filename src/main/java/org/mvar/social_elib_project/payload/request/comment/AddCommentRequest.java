package org.mvar.social_elib_project.payload.request.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.Date;

@Builder
public record AddCommentRequest(
        @NotBlank(message = "Text is required")
        String text
) {
}
