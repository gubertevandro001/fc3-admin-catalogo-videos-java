package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.ValueObject;
import com.fullcycle.admin.catalogo.domain.resource.Resource;

import java.util.Objects;

public class VideoResource extends ValueObject {

    private final VideoMediaType type;
    private final Resource resource;

    public VideoResource(VideoMediaType type, Resource resource) {
        this.type = Objects.requireNonNull(type);
        this.resource = Objects.requireNonNull(resource);
    }

    public static VideoResource with(VideoMediaType type, Resource resource) {
        return new VideoResource(type, resource);
    }

    public VideoMediaType getType() {
        return type;
    }

    public Resource getResource() {
        return resource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoResource that = (VideoResource) o;
        return type == that.type && Objects.equals(resource, that.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, resource);
    }
}
