package org.mvar.social_elib_project.payload.request.item;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateItemRequest(
        @NotBlank(message = "Field name cannot be empty")
        String fieldName,
        @NotBlank(message = "Value cannot be empty")
        String newValue
) {
}
