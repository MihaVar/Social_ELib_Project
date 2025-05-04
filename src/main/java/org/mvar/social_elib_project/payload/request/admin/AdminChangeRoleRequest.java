package org.mvar.social_elib_project.payload.request.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.mvar.social_elib_project.model.Role;

@Builder
public record AdminChangeRoleRequest(
        @NotBlank(message = "User cannot be empty")
        String user,
        Role role
) {
}
