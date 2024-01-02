package com.fullcycle.admin.catalogo.infrastructure.services.impl;

import com.fullcycle.admin.catalogo.domain.resource.Resource;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaType;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import io.vavr.API;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static io.vavr.API.List;

public class GCStorageServiceTest {

    private GCStorageService target;

    private Storage storage;

    private String bucket = "fc3_test";

    @BeforeEach
    public void setUp() {
        this.storage = Mockito.mock(Storage.class);
        this.target = new GCStorageService(this.bucket, this.storage);
    }

    @Test
    public void givenValidResource_whenCallsStore_shouldPersistIt() {

        final var expectedName = IdUtils.uuid();

        final String contentType = API.Match(VideoMediaType.VIDEO).of(
                API.Case(API.$(List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                API.Case(API.$(), "image/jpg")
        );

        final String checksum = IdUtils.uuid();
        final byte[] content = "Conteudo".getBytes();

        final var expectedResource = Resource.with(content, contentType, checksum, VideoMediaType.VIDEO.name());

        final var blob = mockBlob(expectedName, expectedResource);
        Mockito.doReturn(blob).when(storage).create(Mockito.any(BlobInfo.class), Mockito.any());

        this.target.store(expectedName, expectedResource);

        final var captor = ArgumentCaptor.forClass(BlobInfo.class);

        Mockito.verify(storage, Mockito.times(1)).create(captor.capture(), Mockito.eq(expectedResource.content()));

        final var actualBlob = captor.getValue();

        Assertions.assertEquals(this.bucket, actualBlob.getBlobId().getBucket());
        Assertions.assertEquals(expectedName, actualBlob.getName());
        Assertions.assertEquals(expectedResource.getChecksum(), actualBlob.getCrc32cToHexString());
        Assertions.assertEquals(expectedResource.contentType(), actualBlob.getContentType());

    }

    @Test
    public void givenValidResource_whenCallsGet_shouldRetrieveIt() {

        final var expectedName = IdUtils.uuid();

        final String contentType = API.Match(VideoMediaType.VIDEO).of(
                API.Case(API.$(List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                API.Case(API.$(), "image/jpg")
        );

        final String checksum = IdUtils.uuid();
        final byte[] content = "Conteudo".getBytes();

        final var expectedResource = Resource.with(content, contentType, checksum, VideoMediaType.VIDEO.name());

        final var blob = mockBlob(expectedName, expectedResource);
        Mockito.doReturn(blob).when(storage).get(Mockito.anyString(), Mockito.anyString());

        final var actualResource = this.target.get(expectedName).get();

        Mockito.verify(storage, Mockito.times(1)).get(Mockito.eq(this.bucket), Mockito.eq(expectedName));

        Assertions.assertEquals(expectedResource, actualResource);

    }

    @Test
    public void givenInvalidResource_whenCallsGet_shouldBeEmpty() {

        final var expectedName = IdUtils.uuid();

        final String contentType = API.Match(VideoMediaType.VIDEO).of(
                API.Case(API.$(List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                API.Case(API.$(), "image/jpg")
        );

        final String checksum = IdUtils.uuid();
        final byte[] content = "Conteudo".getBytes();

        final var expectedResource = Resource.with(content, contentType, checksum, VideoMediaType.VIDEO.name());

        Mockito.doReturn(null).when(storage).get(Mockito.anyString(), Mockito.anyString());

        final var actualResource = this.target.get(expectedName);

        Assertions.assertTrue(actualResource.isEmpty());
    }

    @Test
    public void givenValidPrefix_whenCallsList_shouldRetrieveAll() {

        final String contentType = API.Match(VideoMediaType.VIDEO).of(
                API.Case(API.$(List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                API.Case(API.$(), "image/jpg")
        );

        final String checksum = IdUtils.uuid();
        final byte[] content = "Conteudo".getBytes();

        final var expectedVideo = Resource.with(content, contentType, checksum, VideoMediaType.VIDEO.name());
        final var expectedBanner = Resource.with(content, contentType, checksum, VideoMediaType.BANNER.name());

        final var expectedPrefix = "media_";
        final var expectedNameVideo = expectedPrefix + IdUtils.uuid();
        final var expectedNameBanner = expectedPrefix + IdUtils.uuid();

        final var blobVideo = mockBlob(expectedNameVideo, expectedVideo);
        final var blobBanner = mockBlob(expectedNameBanner, expectedBanner);

        final var page = Mockito.mock(Page.class);

        Mockito.doReturn(List.of(blobVideo, blobBanner)).when(page).iterateAll();
        Mockito.doReturn(page).when(storage).list(Mockito.anyString(), Mockito.any());

        final var actualResource = this.target.list(expectedPrefix);

        Mockito.verify(storage, Mockito.times(1)).list(Mockito.eq(this.bucket), Mockito.eq(Storage.BlobListOption.prefix(expectedPrefix)));

    }

    @Test
    public void givenValidNames_whenCallsDelete_shouldDeleteAll() {


        final String contentType = API.Match(VideoMediaType.VIDEO).of(
                API.Case(API.$(List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                API.Case(API.$(), "image/jpg")
        );

        final String checksum = IdUtils.uuid();
        final byte[] content = "Conteudo".getBytes();


        final var expectedPrefix = "media_";

        final var expectedNameVideo = expectedPrefix + IdUtils.uuid();
        final var expectedNameBanner = expectedPrefix + IdUtils.uuid();

        final var expectedResources = List.of(expectedNameBanner, expectedNameVideo);

        this.target.deleteAll(expectedResources);

        final var captor = ArgumentCaptor.forClass(List.class);

        Mockito.verify(storage, Mockito.times(1)).delete(captor.capture());

        final var actualResources = ((List<BlobId>) captor.getValue()).stream().map(BlobId::getName).toList();

        Assertions.assertTrue(expectedResources.size() == actualResources.size());
    }

    private Blob mockBlob(final String name, final Resource resource) {
        final var blob = Mockito.mock(Blob.class);
        Mockito.when(blob.getBlobId()).thenReturn(BlobId.of(this.bucket, name));
        Mockito.when(blob.getCrc32cToHexString()).thenReturn(resource.getChecksum());
        Mockito.when(blob.getContent()).thenReturn(resource.content());
        Mockito.when(blob.getContentType()).thenReturn(resource.contentType());
        Mockito.when(blob.getName()).thenReturn(resource.getName());
        return blob;
    }

}
