package org.mvar.social_elib_project.payload.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserChangeUsernameRequest(
        @NotBlank(message = "Username cannot be empty")
        String usersname
) {
}
