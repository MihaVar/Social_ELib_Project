package org.mvar.social_elib_project.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record DeleteItemRequest(
        @NotBlank(message = "Id cannot be empty")
        String id
) {
}
