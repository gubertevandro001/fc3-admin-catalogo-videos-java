package com.fullcycle.admin.catalogo.application.video.media.upload;

import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalogo.domain.video.Video;
import com.fullcycle.admin.catalogo.domain.video.VideoGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoID;

public class DefaultUploadMediaUseCase extends UploadMediaUseCase {

    private final VideoGateway videoGateway;

    private final MediaResourceGateway mediaResourceGateway;

    public DefaultUploadMediaUseCase(VideoGateway videoGateway, MediaResourceGateway mediaResourceGateway) {
        this.videoGateway = videoGateway;
        this.mediaResourceGateway = mediaResourceGateway;
    }

    @Override
    public UploadMediaOutput execute(UploadMediaCommand aCommand) {
        final var id = VideoID.from(aCommand.videoId());
        final var resource = aCommand.videoResource();

        final var aVideo = this.videoGateway.findById(id).orElseThrow(() -> notFound(id));

        switch(resource.getType()) {
            case VIDEO -> aVideo.setVideo(mediaResourceGateway.storeAudioVideo(id, resource));
            case TRAILER -> aVideo.setTrailer(mediaResourceGateway.storeAudioVideo(id, resource));
            case BANNER -> aVideo.setBanner(mediaResourceGateway.storeImage(id, resource));
            case THUMBNAIL -> aVideo.setThumbnail(mediaResourceGateway.storeImage(id, resource));
            case THUMBNAIL_HALF -> aVideo.setThumbnailHalf(mediaResourceGateway.storeImage(id, resource));
        }

        return UploadMediaOutput.with(videoGateway.update(aVideo), resource.getType());
    }

    private NotFoundException notFound(VideoID id) {
        return NotFoundException.with(Video.class, id);
    }

}
