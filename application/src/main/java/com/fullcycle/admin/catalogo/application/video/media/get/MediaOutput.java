package com.fullcycle.admin.catalogo.application.video.media.get;

import com.fullcycle.admin.catalogo.domain.resource.Resource;

public record MediaOutput(
        byte[] content,
        String contentType,
        String name
) {

    public static MediaOutput with(final Resource resource) {
        return new MediaOutput(resource.content(), resource.contentType(), resource.getName());
    }
}
