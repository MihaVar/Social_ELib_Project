package org.mvar.social_elib_project.payload.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AuthRequest(
        @NotBlank(message = "Username cannot be empty")
        String username,
        @NotBlank(message = "Password cannot be empty")
        String password
) {
}
