package com.fullcycle.admin.catalogo.application.video.media.get;

import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalogo.domain.video.Video;
import com.fullcycle.admin.catalogo.domain.video.VideoID;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaType;

import javax.print.attribute.standard.Media;

public class DefaultGetMediaUseCase extends GetMediaUseCase {

    private final MediaResourceGateway mediaResourceGateway;

    public DefaultGetMediaUseCase(MediaResourceGateway mediaResourceGateway) {
        this.mediaResourceGateway = mediaResourceGateway;
    }

    @Override
    public MediaOutput execute(GetMediaCommand aCommand) {
        final var id = VideoID.from(aCommand.videoId());
        final var type = VideoMediaType.of(aCommand.mediaType()).orElseThrow(() -> typeNotFound(aCommand.mediaType()));

        final var resource = this.mediaResourceGateway.getResource(id, type).orElseThrow(() -> notFound(aCommand.videoId(), aCommand.mediaType()));
        return MediaOutput.with(resource);
    }

    private NotFoundException notFound(final String id, final String type) {
        return NotFoundException.with(new Error("Resource %s not foun for video %s".formatted(type, id)));
    }

    private NotFoundException typeNotFound(final String type) {
        return NotFoundException.with(new Error("Media type %s doesn't exists".formatted(type)));
    }
}
