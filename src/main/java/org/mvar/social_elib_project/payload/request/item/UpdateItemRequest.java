package org.mvar.social_elib_project.payload.request.item;

import lombok.Builder;

@Builder
public record UpdateItemRequest(
        String name,
        String author,
        String description,
        String category,
        String publishDate,
        String materialLink
) {
}
