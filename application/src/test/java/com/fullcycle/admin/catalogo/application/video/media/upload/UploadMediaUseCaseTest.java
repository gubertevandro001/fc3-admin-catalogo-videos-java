package com.fullcycle.admin.catalogo.application.video.media.upload;

import com.fullcycle.admin.catalogo.application.Fixture;
import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.video.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

public class UploadMediaUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultUploadMediaUseCase useCase;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Mock
    private VideoGateway videoGateway;


    @Override
    protected List<Object> getMocks() {
        return List.of(mediaResourceGateway, videoGateway);
    }

    @Test
    public void givenCommandToUpload_whenIsValid_shouldUpdateVideoMediaAndPersistIt() {

        final var aVideo = Fixture.Videos.systemDesign();
        final var expectedId = aVideo.getId();
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedResource = Fixture.Videos.resource(expectedType);
        final var expectedVideoResource = VideoResource.with(expectedType, expectedResource);
        final var expectedMedia = AudioVideoMedia.with(IdUtils.uuid(), expectedType.name(), IdUtils.uuid());

        Mockito.when(videoGateway.findById(Mockito.any())).thenReturn(Optional.of(aVideo));

        Mockito.when(mediaResourceGateway.storeAudioVideo(Mockito.any(), Mockito.any())).thenReturn(expectedMedia);

        Mockito.when(videoGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aCommand = UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

        final var actualOutput = useCase.execute(aCommand);

        Assertions.assertEquals(expectedType, actualOutput.mediaType());
        Assertions.assertEquals(expectedId.getValue(), actualOutput.videoId());

        Mockito.verify(videoGateway, Mockito.times(1)).findById(Mockito.eq(expectedId));

        Mockito.verify(mediaResourceGateway, Mockito.times(1)).storeAudioVideo(Mockito.eq(expectedId), Mockito.eq(expectedVideoResource));

        Mockito.verify(videoGateway, Mockito.times(1)).update(Mockito.argThat(actualVideo ->
            Objects.equals(expectedMedia, actualVideo.getVideo().get())
            && actualVideo.getTrailer().isEmpty()
            && actualVideo.getBanner().isEmpty()
            && actualVideo.getThumbnail().isEmpty()
            && actualVideo.getThumbnailHalf().isEmpty()
        ));
    }

    @Test
    public void givenCommandToUpload_whenIsValid_shouldUpdateTrailerMediaAndPersistIt() {

        final var aVideo = Fixture.Videos.systemDesign();
        final var expectedId = aVideo.getId();
        final var expectedType = VideoMediaType.TRAILER;
        final var expectedResource = Fixture.Videos.resource(expectedType);
        final var expectedVideoResource = VideoResource.with(expectedType, expectedResource);
        final var expectedMedia = AudioVideoMedia.with(IdUtils.uuid(), expectedType.name(), IdUtils.uuid());

        Mockito.when(videoGateway.findById(Mockito.any())).thenReturn(Optional.of(aVideo));

        Mockito.when(mediaResourceGateway.storeAudioVideo(Mockito.any(), Mockito.any())).thenReturn(expectedMedia);

        Mockito.when(videoGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aCommand = UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

        final var actualOutput = useCase.execute(aCommand);

        Assertions.assertEquals(expectedType, actualOutput.mediaType());
        Assertions.assertEquals(expectedId.getValue(), actualOutput.videoId());

        Mockito.verify(videoGateway, Mockito.times(1)).findById(Mockito.eq(expectedId));

        Mockito.verify(mediaResourceGateway, Mockito.times(1)).storeAudioVideo(Mockito.eq(expectedId), Mockito.eq(expectedVideoResource));

        Mockito.verify(videoGateway, Mockito.times(1)).update(Mockito.argThat(actualVideo ->
                Objects.equals(expectedMedia, actualVideo.getTrailer().get())
                        && actualVideo.getVideo().isEmpty()
                        && actualVideo.getBanner().isEmpty()
                        && actualVideo.getThumbnail().isEmpty()
                        && actualVideo.getThumbnailHalf().isEmpty()
        ));
    }

    @Test
    public void givenCommandToUpload_whenIsValid_shouldUpdateBannerMediaAndPersistIt() {

        final var aVideo = Fixture.Videos.systemDesign();
        final var expectedId = aVideo.getId();
        final var expectedType = VideoMediaType.BANNER;
        final var expectedResource = Fixture.Videos.resource(expectedType);
        final var expectedVideoResource = VideoResource.with(expectedType, expectedResource);
        final var expectedMedia = ImageMedia.with(IdUtils.uuid(), expectedType.name(), IdUtils.uuid());

        Mockito.when(videoGateway.findById(Mockito.any())).thenReturn(Optional.of(aVideo));

        Mockito.when(mediaResourceGateway.storeImage(Mockito.any(), Mockito.any())).thenReturn(expectedMedia);

        Mockito.when(videoGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aCommand = UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

        final var actualOutput = useCase.execute(aCommand);

        Assertions.assertEquals(expectedType, actualOutput.mediaType());
        Assertions.assertEquals(expectedId.getValue(), actualOutput.videoId());

        Mockito.verify(videoGateway, Mockito.times(1)).findById(Mockito.eq(expectedId));

        Mockito.verify(mediaResourceGateway, Mockito.times(1)).storeImage(Mockito.eq(expectedId), Mockito.eq(expectedVideoResource));

        Mockito.verify(videoGateway, Mockito.times(1)).update(Mockito.argThat(actualVideo ->
                Objects.equals(expectedMedia, actualVideo.getBanner().get())
                        && actualVideo.getVideo().isEmpty()
                        && actualVideo.getTrailer().isEmpty()
                        && actualVideo.getThumbnail().isEmpty()
                        && actualVideo.getThumbnailHalf().isEmpty()
        ));
    }

    @Test
    public void givenCommandToUpload_whenIsValid_shouldUpdateThumbMediaAndPersistIt() {

        final var aVideo = Fixture.Videos.systemDesign();
        final var expectedId = aVideo.getId();
        final var expectedType = VideoMediaType.THUMBNAIL;
        final var expectedResource = Fixture.Videos.resource(expectedType);
        final var expectedVideoResource = VideoResource.with(expectedType, expectedResource);
        final var expectedMedia = ImageMedia.with(IdUtils.uuid(), expectedType.name(), IdUtils.uuid());

        Mockito.when(videoGateway.findById(Mockito.any())).thenReturn(Optional.of(aVideo));

        Mockito.when(mediaResourceGateway.storeImage(Mockito.any(), Mockito.any())).thenReturn(expectedMedia);

        Mockito.when(videoGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aCommand = UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

        final var actualOutput = useCase.execute(aCommand);

        Assertions.assertEquals(expectedType, actualOutput.mediaType());
        Assertions.assertEquals(expectedId.getValue(), actualOutput.videoId());

        Mockito.verify(videoGateway, Mockito.times(1)).findById(Mockito.eq(expectedId));

        Mockito.verify(mediaResourceGateway, Mockito.times(1)).storeImage(Mockito.eq(expectedId), Mockito.eq(expectedVideoResource));

        Mockito.verify(videoGateway, Mockito.times(1)).update(Mockito.argThat(actualVideo ->
                Objects.equals(expectedMedia, actualVideo.getThumbnail().get())
                        && actualVideo.getVideo().isEmpty()
                        && actualVideo.getTrailer().isEmpty()
                        && actualVideo.getBanner().isEmpty()
                        && actualVideo.getThumbnailHalf().isEmpty()
        ));
    }

    @Test
    public void givenCommandToUpload_whenIsValid_shouldUpdateThumbHalfMediaAndPersistIt() {

        final var aVideo = Fixture.Videos.systemDesign();
        final var expectedId = aVideo.getId();
        final var expectedType = VideoMediaType.THUMBNAIL_HALF;
        final var expectedResource = Fixture.Videos.resource(expectedType);
        final var expectedVideoResource = VideoResource.with(expectedType, expectedResource);
        final var expectedMedia = ImageMedia.with(IdUtils.uuid(), expectedType.name(), IdUtils.uuid());

        Mockito.when(videoGateway.findById(Mockito.any())).thenReturn(Optional.of(aVideo));

        Mockito.when(mediaResourceGateway.storeImage(Mockito.any(), Mockito.any())).thenReturn(expectedMedia);

        Mockito.when(videoGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aCommand = UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

        final var actualOutput = useCase.execute(aCommand);

        Assertions.assertEquals(expectedType, actualOutput.mediaType());
        Assertions.assertEquals(expectedId.getValue(), actualOutput.videoId());

        Mockito.verify(videoGateway, Mockito.times(1)).findById(Mockito.eq(expectedId));

        Mockito.verify(mediaResourceGateway, Mockito.times(1)).storeImage(Mockito.eq(expectedId), Mockito.eq(expectedVideoResource));

        Mockito.verify(videoGateway, Mockito.times(1)).update(Mockito.argThat(actualVideo ->
                Objects.equals(expectedMedia, actualVideo.getThumbnailHalf().get())
                        && actualVideo.getVideo().isEmpty()
                        && actualVideo.getTrailer().isEmpty()
                        && actualVideo.getBanner().isEmpty()
                        && actualVideo.getThumbnail().isEmpty()
        ));
    }

    @Test
    public void givenCommandToUpload_whenVideoIsInvalid_shouldReturnNotFound() {

        final var aVideo = Fixture.Videos.systemDesign();
        final var expectedId = aVideo.getId();
        final var expectedType = VideoMediaType.THUMBNAIL_HALF;
        final var expectedResource = Fixture.Videos.resource(expectedType);
        final var expectedVideoResource = VideoResource.with(expectedType, expectedResource);
        final var expectedErrorMessage = "Video with ID %s was not found".formatted(expectedId.getValue());

        Mockito.when(videoGateway.findById(Mockito.any())).thenReturn(Optional.empty());

        final var aCommand = UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

        final var actualException = Assertions.assertThrows(NotFoundException.class, () -> useCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());


    }

}
