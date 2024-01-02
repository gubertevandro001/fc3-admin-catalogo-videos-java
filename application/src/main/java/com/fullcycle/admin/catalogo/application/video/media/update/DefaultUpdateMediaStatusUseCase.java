package com.fullcycle.admin.catalogo.application.video.media.update;

import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.video.*;

public class DefaultUpdateMediaStatusUseCase extends UpdateMediaStatusUseCase {

    private final VideoGateway videoGateway;

    public DefaultUpdateMediaStatusUseCase(VideoGateway videoGateway) {
        this.videoGateway = videoGateway;
    }

    @Override
    public void execute(UpdateMediaStatusCommand aCommand) {
        final var id = VideoID.from(aCommand.videoId());
        final var resourceId = aCommand.resourceId();
        final var folder = aCommand.folder();
        final var filename = aCommand.filename();

        final var aVideo = this.videoGateway.findById(id).orElseThrow(() -> notFound(id));

        final var encodedPath = "%s/%s".formatted(folder, filename);

        if (matches(resourceId, aVideo.getVideo().orElse(null))) {
            updateVideo(VideoMediaType.VIDEO, aCommand.status(), aVideo, encodedPath);
        } else if (matches(resourceId, aVideo.getTrailer().orElse(null))) {
            updateVideo(VideoMediaType.TRAILER, aCommand.status(), aVideo, encodedPath);
        }

    }

    private boolean matches(final String id, final AudioVideoMedia media) {
        if (media == null) {
            return false;
        }
        return media.getId().equals(id);
    }

    private void updateVideo(final VideoMediaType type, final MediaStatus status, final Video video, final String encodedPath) {
        switch (status) {
            case PENDING -> {}
            case PROCESSING -> video.processing(type);
            case COMPLETED -> video.completed(type, encodedPath);
        }
    }

    private NotFoundException notFound(VideoID id) {
        return NotFoundException.with(Video.class, id);
    }
}
