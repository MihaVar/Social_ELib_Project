package org.mvar.social_elib_project.payload.request.user;

import lombok.Builder;

@Builder
public record AddExpertAccomplishmentRequest(
        String accomplishment
) {
}
