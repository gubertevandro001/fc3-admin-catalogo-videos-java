package com.fullcycle.admin.catalogo.infrastructure.services.local;

import com.fullcycle.admin.catalogo.domain.resource.Resource;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaType;
import io.vavr.API;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static io.vavr.API.List;

public class InMemoryStorageServiceTest {

    private InMemoryStorageService target = new InMemoryStorageService();

    @BeforeEach
    public void setUp() {
        this.target.reset();
    }

    @Test
    public void givenValidResource_whenCallsStore_shouldStoreIt() {

        final String contentType = API.Match(VideoMediaType.VIDEO).of(
                API.Case(API.$(List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                API.Case(API.$(), "image/jpg")
        );

        final String checksum = IdUtils.uuid();
        final byte[] content = "Conteudo".getBytes();

        final var expectedResource = Resource.with(content, contentType, checksum, VideoMediaType.VIDEO.name());
        final var expectedName = IdUtils.uuid();

        target.store(expectedName, expectedResource);

        Assertions.assertEquals(expectedResource, target.storage().get(expectedName));

    }

    @Test
    public void givenValidResource_whenCallsGet_shouldRetrieveIt() {

        final String contentType = API.Match(VideoMediaType.VIDEO).of(
                API.Case(API.$(List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                API.Case(API.$(), "image/jpg")
        );

        final String checksum = IdUtils.uuid();
        final byte[] content = "Conteudo".getBytes();

        final var expectedResource = Resource.with(content, contentType, checksum, VideoMediaType.VIDEO.name());
        final var expectedName = IdUtils.uuid();

        target.storage().put(expectedName, expectedResource);

        final var actualResource = target.get(expectedName).get();


        Assertions.assertEquals(expectedResource, actualResource);

    }

    @Test
    public void givenInvalidResource_whenCallsGet_shouldBeEmpty() {

        final var expectedName = IdUtils.uuid();

        final var actualResource = target.get(expectedName);

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


        final var expectedResource = Resource.with(content, contentType, checksum, VideoMediaType.VIDEO.name());
        final var expectedNames = List.of("video_" + IdUtils.uuid(), "video_" + IdUtils.uuid(), "video_" + IdUtils.uuid(), "video_" + IdUtils.uuid());

        expectedNames.forEach(name -> target.storage().put(name, expectedResource));

        Assertions.assertEquals(4, target.storage().size());

        final var actualResource = target.list("video");


        Assertions.assertTrue(expectedNames.size() == actualResource.size()
            && expectedNames.containsAll(actualResource));

    }

    @Test
    public void givenValidNames_whenCallsDelete_shouldDeleteAll() {

        final String contentType = API.Match(VideoMediaType.VIDEO).of(
                API.Case(API.$(List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                API.Case(API.$(), "image/jpg")
        );

        final String checksum = IdUtils.uuid();
        final byte[] content = "Conteudo".getBytes();


        final var expectedResource = Resource.with(content, contentType, checksum, VideoMediaType.VIDEO.name());
        final var expectedNames = Set.of("video_" + IdUtils.uuid(), "video_" + IdUtils.uuid(), "video_" + IdUtils.uuid(), "video_" + IdUtils.uuid());

        expectedNames.forEach(name -> target.storage().put(name, expectedResource));

        Assertions.assertEquals(4, target.storage().size());

        target.deleteAll(expectedNames);

        Assertions.assertEquals(0, target.storage().size());

        final var actualKeys = target.storage().keySet();

        Assertions.assertTrue(expectedNames.size() == actualKeys.size());

    }

}
