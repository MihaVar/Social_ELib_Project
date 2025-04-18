package org.mvar.social_elib_project.payload.request.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AdminDeleteItemRequest(
        @NotBlank(message = "Item id should not be empty")
        String itemId
) {
}
