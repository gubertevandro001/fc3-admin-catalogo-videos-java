package com.fullcycle.admin.catalogo.infrastructure.video;

import com.fullcycle.admin.catalogo.domain.resource.Resource;
import com.fullcycle.admin.catalogo.domain.video.*;
import com.fullcycle.admin.catalogo.infrastructure.configuration.properties.storage.StorageProperties;
import com.fullcycle.admin.catalogo.infrastructure.services.StorageService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DefaultMediaResourceGateway implements MediaResourceGateway {

    private final String filenamePattern;
    private final String locationPattern;
    private final StorageService storageService;

    public DefaultMediaResourceGateway(StorageProperties props, StorageService storageService) {
        this.filenamePattern = props.getFilenamePattern();
        this.locationPattern = props.getLocationPattern();
        this.storageService = storageService;
    }

    @Override
    public AudioVideoMedia storeAudioVideo(VideoID id, VideoResource videoResource) {
        final var filepath = filepath(id, videoResource.getType());
        final var aResource = videoResource.getResource();
        store(filepath, aResource);
        return AudioVideoMedia.with(aResource.getChecksum(), aResource.getName(), filepath);
    }

    @Override
    public ImageMedia storeImage(VideoID id, VideoResource videoResource) {
        final var filepath = filepath(id, videoResource.getType());
        final var aResource = videoResource.getResource();
        store(filepath, aResource);
        return ImageMedia.with(aResource.getChecksum(), aResource.getName(), filepath);
    }

    @Override
    public Optional<Resource> getResource(VideoID id, VideoMediaType type) {
        return this.storageService.get(filepath(id, type));
    }

    @Override
    public void clearResources(VideoID id) {
        final var ids = this.storageService.list(folder(id));
        this.storageService.deleteAll(ids);
    }

    private String filename(VideoMediaType type) {
        return filenamePattern.replace("{type}", type.name());
    }
    private String folder(VideoID id) {
        return locationPattern.replace("{videoId}", id.getValue());
    }
    private String filepath(VideoID id, VideoMediaType type) {
        return folder(id).concat("/").concat(filename(type));
    }

    private void store(String filepath, Resource resource) {
        this.storageService.store(filepath, resource);
    }

}
