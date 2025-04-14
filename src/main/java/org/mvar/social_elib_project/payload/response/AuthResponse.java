package org.mvar.social_elib_project.payload.response;

import lombok.Builder;

@Builder
public record AuthResponse(
        String token
) {
}
