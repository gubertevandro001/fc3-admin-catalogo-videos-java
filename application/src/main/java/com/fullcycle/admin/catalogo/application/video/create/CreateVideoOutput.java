package com.fullcycle.admin.catalogo.application.video.create;

import com.fullcycle.admin.catalogo.domain.video.Video;

public record CreateVideoOutput(String id) {

    public static CreateVideoOutput with(final Video aVideo) {
        return new CreateVideoOutput(aVideo.getId().getValue());
    }
}
