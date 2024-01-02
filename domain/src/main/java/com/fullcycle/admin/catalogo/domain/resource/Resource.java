package com.fullcycle.admin.catalogo.domain.resource;

import com.fullcycle.admin.catalogo.domain.ValueObject;

import java.util.Arrays;
import java.util.Objects;

public class  Resource extends ValueObject {

    private final byte[] content;
    private final String contentType;
    private final String checksum;

    private final String name;

    private Resource(final byte[] content, final String contentType, final String checksum, String name) {
        this.content = Objects.requireNonNull(content);
        this.contentType = Objects.requireNonNull(contentType);
        this.checksum = Objects.requireNonNull(checksum);
        this.name = Objects.requireNonNull(name);
    }

    public static Resource with(final byte[] content, final String contentType, final String checksum, final String name) {
        return new Resource(content, contentType, checksum, name);
    }

    public byte[] content() {
        return content;
    }

    public String contentType() {
        return contentType;
    }

    public String getChecksum() {
        return checksum;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Arrays.equals(content, resource.content) && Objects.equals(contentType, resource.contentType) && Objects.equals(checksum, resource.checksum) && Objects.equals(name, resource.name);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(contentType, checksum, name);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }
}
