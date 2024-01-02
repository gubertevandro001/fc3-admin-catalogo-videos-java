package com.fullcycle.admin.catalogo.application.video.media.update;

import com.fullcycle.admin.catalogo.application.Fixture;
import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.video.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.List;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

public class UpdateMediaStatusUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultUpdateMediaStatusUseCase useCase;

    @Mock
    private VideoGateway videoGateway;


    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway);
    }

    @Test
    public void givenCommandForVideo_whenIsValid_shouldUpdateStatusAndEncodedLocation() {

        final var expectedStatus = MediaStatus.COMPLETED;
        final var expectedFolder = "encoded_media";
        final var expectedFilename = "filename.mp4";
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedMedia = AudioVideoMedia.with(IdUtils.uuid(), expectedType.name(), IdUtils.uuid());

        final var aVideo = Fixture.Videos.systemDesign().setVideo(expectedMedia);

        final var expectedId = aVideo.getId();

        when(videoGateway.findById(any())).thenReturn(Optional.of(aVideo));

        when(videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var aCommand = UpdateMediaStatusCommand.with(expectedStatus, expectedId.getValue(), expectedMedia.getId(), expectedFolder, expectedFilename);

        this.useCase.execute(aCommand);

        verify(videoGateway, times(1)).findById(eq(expectedId));

        final var captor = ArgumentCaptor.forClass(Video.class);

        verify(videoGateway, times(1)).update(captor.capture());

        final var actualVideo = captor.getValue();

        Assertions.assertTrue(actualVideo.getTrailer().isEmpty());

        final var actualVideoMedia = actualVideo.getVideo().get();

        Assertions.assertEquals(expectedMedia.getId(), actualVideoMedia.getId());
        Assertions.assertEquals(expectedMedia.rawLocation(), actualVideoMedia.rawLocation());
        Assertions.assertEquals(expectedMedia.checkSum(), actualVideoMedia.checkSum());
        Assertions.assertEquals(expectedStatus, actualVideoMedia.status());
        Assertions.assertEquals(expectedFolder.concat("/").concat(expectedFilename), actualVideoMedia.encodedLocation());

    }

    @Test
    public void givenCommandForTrailer_whenIsValid_shouldUpdateStatusAndEncodedLocation() {

        final var expectedStatus = MediaStatus.COMPLETED;
        final var expectedFolder = "encoded_media";
        final var expectedFilename = "filename.mp4";
        final var expectedType = VideoMediaType.TRAILER;
        final var expectedMedia = AudioVideoMedia.with(IdUtils.uuid(), expectedType.name(), IdUtils.uuid());

        final var aVideo = Fixture.Videos.systemDesign().setTrailer(expectedMedia);

        final var expectedId = aVideo.getId();

        when(videoGateway.findById(any())).thenReturn(Optional.of(aVideo));

        when(videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var aCommand = UpdateMediaStatusCommand.with(expectedStatus, expectedId.getValue(), expectedMedia.getId(), expectedFolder, expectedFilename);

        this.useCase.execute(aCommand);

        verify(videoGateway, times(1)).findById(eq(expectedId));

        final var captor = ArgumentCaptor.forClass(Video.class);

        verify(videoGateway, times(1)).update(captor.capture());

        final var actualVideo = captor.getValue();

        Assertions.assertTrue(actualVideo.getVideo().isEmpty());

        final var actualVideoMedia = actualVideo.getTrailer().get();

        Assertions.assertEquals(expectedMedia.getId(), actualVideoMedia.getId());
        Assertions.assertEquals(expectedMedia.rawLocation(), actualVideoMedia.rawLocation());
        Assertions.assertEquals(expectedMedia.checkSum(), actualVideoMedia.checkSum());
        Assertions.assertEquals(expectedStatus, actualVideoMedia.status());
        Assertions.assertEquals(expectedFolder.concat("/").concat(expectedFilename), actualVideoMedia.encodedLocation());

    }

    @Test
    public void givenCommandForTrailer_whenIsInvalid_shouldDoNothing() {

        final var expectedStatus = MediaStatus.COMPLETED;
        final var expectedFolder = "encoded_media";
        final var expectedFilename = "filename.mp4";
        final var expectedType = VideoMediaType.TRAILER;
        final var expectedMedia = AudioVideoMedia.with(IdUtils.uuid(), expectedType.name(), IdUtils.uuid());

        final var aVideo = Fixture.Videos.systemDesign().setTrailer(expectedMedia);

        final var expectedId = aVideo.getId();

        when(videoGateway.findById(any())).thenReturn(Optional.of(aVideo));

        final var aCommand = UpdateMediaStatusCommand.with(expectedStatus, expectedId.getValue(), "randomId", expectedFolder, expectedFilename);

        this.useCase.execute(aCommand);

        verify(videoGateway, times(0)).update(any());

    }

    @Test
    public void givenCommandForVideo_whenIsValidForProcessing_shouldUpdateStatusAndEncodedLocation() {

        final var expectedStatus = MediaStatus.PROCESSING;
        final String expectedFolder = null;
        final String expectedFilename = null;
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedMedia = AudioVideoMedia.with(IdUtils.uuid(), expectedType.name(), IdUtils.uuid());

        final var aVideo = Fixture.Videos.systemDesign().setVideo(expectedMedia);

        final var expectedId = aVideo.getId();

        when(videoGateway.findById(any())).thenReturn(Optional.of(aVideo));

        when(videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var aCommand = UpdateMediaStatusCommand.with(expectedStatus, expectedId.getValue(), expectedMedia.getId(), expectedFolder, expectedFilename);

        this.useCase.execute(aCommand);

        verify(videoGateway, times(1)).findById(eq(expectedId));

        final var captor = ArgumentCaptor.forClass(Video.class);

        verify(videoGateway, times(1)).update(captor.capture());

        final var actualVideo = captor.getValue();

        Assertions.assertTrue(actualVideo.getTrailer().isEmpty());

        final var actualVideoMedia = actualVideo.getVideo().get();

        Assertions.assertEquals(expectedMedia.getId(), actualVideoMedia.getId());
        Assertions.assertEquals(expectedMedia.rawLocation(), actualVideoMedia.rawLocation());
        Assertions.assertEquals(expectedMedia.checkSum(), actualVideoMedia.checkSum());
        Assertions.assertEquals(expectedStatus, actualVideoMedia.status());
        Assertions.assertTrue(actualVideoMedia.encodedLocation().isBlank());

    }

    @Test
    public void givenCommandForTrailer_whenIsValidForProcessing_shouldUpdateStatusAndEncodedLocation() {

        final var expectedStatus = MediaStatus.PROCESSING;
        final String expectedFolder = null;
        final String expectedFilename = null;
        final var expectedType = VideoMediaType.TRAILER;
        final var expectedMedia = AudioVideoMedia.with(IdUtils.uuid(), expectedType.name(), IdUtils.uuid());

        final var aVideo = Fixture.Videos.systemDesign().setVideo(expectedMedia);

        final var expectedId = aVideo.getId();

        when(videoGateway.findById(any())).thenReturn(Optional.of(aVideo));

        when(videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var aCommand = UpdateMediaStatusCommand.with(expectedStatus, expectedId.getValue(), expectedMedia.getId(), expectedFolder, expectedFilename);

        this.useCase.execute(aCommand);

        verify(videoGateway, times(1)).findById(eq(expectedId));


        final var captor = ArgumentCaptor.forClass(Video.class);


        verify(videoGateway, times(1)).update(captor.capture());

        final var actualVideo = captor.getValue();

        Assertions.assertTrue(actualVideo.getTrailer().isEmpty());

        final var actualVideoMedia = actualVideo.getVideo().get();

        Assertions.assertEquals(expectedMedia.getId(), actualVideoMedia.getId());
        Assertions.assertEquals(expectedMedia.rawLocation(), actualVideoMedia.rawLocation());
        Assertions.assertEquals(expectedMedia.checkSum(), actualVideoMedia.checkSum());
        Assertions.assertEquals(expectedStatus, actualVideoMedia.status());
        Assertions.assertTrue(actualVideoMedia.encodedLocation().isBlank());

    }

}
