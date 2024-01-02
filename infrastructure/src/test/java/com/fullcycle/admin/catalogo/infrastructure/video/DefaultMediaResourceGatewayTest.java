package com.fullcycle.admin.catalogo.infrastructure.video;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.resource.Resource;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.video.*;
import com.fullcycle.admin.catalogo.infrastructure.services.StorageService;
import com.fullcycle.admin.catalogo.infrastructure.services.local.InMemoryStorageService;
import io.vavr.API;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static io.vavr.API.List;

@IntegrationTest
public class DefaultMediaResourceGatewayTest {

    @Autowired
    private MediaResourceGateway mediaResourceGateway;

    @Autowired
    private StorageService storageService;

    @BeforeEach
    public void setUp() {
        storageService().reset();
    }


    @Test
    public void testInjection() {
        Assertions.assertNotNull(mediaResourceGateway);
        Assertions.assertNotNull(storageService);
    }

    @Test
    public void givenValidResource_whenCallsStorageAudioVideo_shouldStoreIt() {

        final var expectedVideoId = VideoID.unique();
        final var expectedType = VideoMediaType.VIDEO;

        final var expectedName = IdUtils.uuid();

        final String contentType = API.Match(VideoMediaType.VIDEO).of(
                API.Case(API.$(List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                API.Case(API.$(), "image/jpg")
        );

        final String checksum = IdUtils.uuid();
        final byte[] content = "Conteudo".getBytes();

        final var expectedResource = Resource.with(content, contentType, checksum, VideoMediaType.VIDEO.name());
        final var expectedLocation = "videoId-%s/type-%s".formatted(expectedVideoId.getValue(), expectedType.name());
        final var expectedStatus = MediaStatus.PENDING;
        final var expectedEncodedLocation = "";

        final var actualMedia =
                this.mediaResourceGateway.storeAudioVideo(expectedVideoId, VideoResource.with(expectedType, expectedResource));

        Assertions.assertNotNull(actualMedia.getId());
        Assertions.assertEquals(expectedLocation, actualMedia.rawLocation());
        Assertions.assertEquals(expectedResource.getName(), actualMedia.name());
        Assertions.assertEquals(expectedResource.getChecksum(), actualMedia.checkSum());
        Assertions.assertEquals(expectedStatus, actualMedia.status());
        Assertions.assertEquals(expectedEncodedLocation, actualMedia.encodedLocation());

        final var actualStored = storageService().storage().get(expectedLocation);

        Assertions.assertEquals(expectedResource, actualStored);
    }

    @Test
    public void givenValidResource_whenCallsStorageImage_shouldStoreIt() {

        final var expectedVideoId = VideoID.unique();
        final var expectedType = VideoMediaType.BANNER;

        final var expectedName = IdUtils.uuid();

        final String contentType = API.Match(VideoMediaType.VIDEO).of(
                API.Case(API.$(List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                API.Case(API.$(), "image/jpg")
        );

        final String checksum = IdUtils.uuid();
        final byte[] content = "Conteudo".getBytes();

        final var expectedResource = Resource.with(content, contentType, checksum, VideoMediaType.VIDEO.name());
        final var expectedLocation = "videoId-%s/type-%s".formatted(expectedVideoId.getValue(), expectedType.name());

        final var actualMedia =
                this.mediaResourceGateway.storeImage(expectedVideoId, VideoResource.with(expectedType, expectedResource));

        Assertions.assertNotNull(actualMedia.getId());
        Assertions.assertEquals(expectedLocation, actualMedia.location());
        Assertions.assertEquals(expectedResource.getName(), actualMedia.name());
        Assertions.assertEquals(expectedResource.getChecksum(), actualMedia.checkSum());

        final var actualStored = storageService().storage().get(expectedLocation);

        Assertions.assertEquals(expectedResource, actualStored);
    }

    @Test
    public void givenValidVideoId_whenCallsClearResources_shouldDeleteAll() {

        final var videoOne = VideoID.unique();
        final var videoTwo = VideoID.unique();

        final String contentType = API.Match(VideoMediaType.VIDEO).of(
                API.Case(API.$(List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                API.Case(API.$(), "image/jpg")
        );

        final String checksum = IdUtils.uuid();
        final byte[] content = "Conteudo".getBytes();

        final var expectedResource = Resource.with(content, contentType, checksum, VideoMediaType.VIDEO.name());

        final var toBeDeleted = new ArrayList<String>();
        toBeDeleted.add("videoId-%s/type-%s".formatted(videoOne.getValue(), VideoMediaType.VIDEO.name()));
        toBeDeleted.add("videoId-%s/type-%s".formatted(videoOne.getValue(), VideoMediaType.TRAILER.name()));
        toBeDeleted.add("videoId-%s/type-%s".formatted(videoOne.getValue(), VideoMediaType.BANNER.name()));

        final var expectedValues = new ArrayList<String>();
        expectedValues.add("videoId-%s/type-%s".formatted(videoTwo.getValue(), VideoMediaType.VIDEO.name()));
        expectedValues.add("videoId-%s/type-%s".formatted(videoTwo.getValue(), VideoMediaType.TRAILER.name()));

        toBeDeleted.forEach(id -> storageService().store(id, Resource.with(content, contentType, checksum, VideoMediaType.VIDEO.name())));
        expectedValues.forEach(id -> storageService().store(id, Resource.with(content, contentType, checksum, VideoMediaType.VIDEO.name())));

        Assertions.assertEquals(5, storageService().storage().size());

        this.mediaResourceGateway.clearResources(videoOne);

        Assertions.assertEquals(2, storageService().storage().size());

        final var actualKeys = storageService().storage().keySet();

        Assertions.assertTrue(actualKeys.size() == expectedValues.size());

    }

    @Test
    public void givenValidVideoId_whenCallsGetResource_shouldReturnIt() {

        final var videoOne = VideoID.unique();

        final String contentType = API.Match(VideoMediaType.VIDEO).of(
                API.Case(API.$(List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                API.Case(API.$(), "image/jpg")
        );

        final String checksum = IdUtils.uuid();
        final byte[] content = "Conteudo".getBytes();

        final var expectedResource = Resource.with(content, contentType, checksum, VideoMediaType.VIDEO.name());

        final var expectedValues = new ArrayList<String>();
        expectedValues.add("videoId-%s/type-%s".formatted(videoOne.getValue(), VideoMediaType.VIDEO.name()));
        expectedValues.add("videoId-%s/type-%s".formatted(videoOne.getValue(), VideoMediaType.TRAILER.name()));
        expectedValues.add("videoId-%s/type-%s".formatted(videoOne.getValue(), VideoMediaType.BANNER.name()));

        expectedValues.forEach(id -> storageService().store(id, Resource.with(content, contentType, checksum, VideoMediaType.VIDEO.name())));

        Assertions.assertEquals(3, storageService().storage().size());

        final var actualResult = this.mediaResourceGateway.getResource(videoOne, VideoMediaType.VIDEO).get();

        Assertions.assertEquals(expectedResource, actualResult);

    }

    @Test
    public void givenInvalidType_whenCallsGetResource_shouldReturnEmpty() {

        final var videoOne = VideoID.unique();

        final String contentType = API.Match(VideoMediaType.VIDEO).of(
                API.Case(API.$(List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                API.Case(API.$(), "image/jpg")
        );

        final String checksum = IdUtils.uuid();
        final byte[] content = "Conteudo".getBytes();

        final var expectedResource = Resource.with(content, contentType, checksum, VideoMediaType.VIDEO.name());

        final var expectedValues = new ArrayList<String>();
        expectedValues.add("videoId-%s/type-%s".formatted(videoOne.getValue(), VideoMediaType.VIDEO.name()));
        expectedValues.add("videoId-%s/type-%s".formatted(videoOne.getValue(), VideoMediaType.TRAILER.name()));
        expectedValues.add("videoId-%s/type-%s".formatted(videoOne.getValue(), VideoMediaType.BANNER.name()));

        expectedValues.forEach(id -> storageService().store(id, Resource.with(content, contentType, checksum, VideoMediaType.VIDEO.name())));

        Assertions.assertEquals(3, storageService().storage().size());

        final var actualResult = this.mediaResourceGateway.getResource(videoOne, VideoMediaType.THUMBNAIL);

        Assertions.assertTrue(actualResult.isEmpty());

    }

    private InMemoryStorageService storageService() {
        return (InMemoryStorageService) storageService;
    }
}
