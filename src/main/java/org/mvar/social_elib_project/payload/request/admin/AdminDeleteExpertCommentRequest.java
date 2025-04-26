package org.mvar.social_elib_project.payload.request.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AdminDeleteExpertCommentRequest(
        @NotBlank(message = "Expert comment id cannot be empty")
        long expertCommentId
) {
}
