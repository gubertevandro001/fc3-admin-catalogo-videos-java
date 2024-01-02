package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.ValueObject;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;

import java.util.Objects;

public class ImageMedia extends ValueObject {

    private String id;
    private final String checkSum;
    private final String name;
    private final String location;

    private ImageMedia(final String id, final String checkSum, final String name, final String location) {
        this.id = id;
        this.checkSum = Objects.requireNonNull(checkSum);
        this.name = Objects.requireNonNull(name);
        this.location = Objects.requireNonNull(location);
    }

    public static ImageMedia with(final String checkSum, final String name, final String location) {
        return new ImageMedia(IdUtils.uuid(), checkSum, name, location);
    }

    public static ImageMedia with(final String id, final String checkSum, final String name, final String location) {
        return new ImageMedia(id, checkSum, name, location);
    }

    public String getId() {
        return id;
    }

    public String checkSum() {
        return checkSum;
    }

    public String name() {
        return name;
    }

    public String location() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageMedia that = (ImageMedia) o;
        return Objects.equals(checkSum, that.checkSum) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkSum, location);
    }
}