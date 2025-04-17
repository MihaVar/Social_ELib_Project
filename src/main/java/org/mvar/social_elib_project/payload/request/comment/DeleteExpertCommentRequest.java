package org.mvar.social_elib_project.payload.request.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record DeleteExpertCommentRequest(
        @NotBlank(message = "Id is required")
        String id
) {
}
