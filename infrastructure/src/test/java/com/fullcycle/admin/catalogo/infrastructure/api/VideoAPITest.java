package com.fullcycle.admin.catalogo.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalogo.ControllerTest;
import com.fullcycle.admin.catalogo.application.video.create.CreateVideoCommand;
import com.fullcycle.admin.catalogo.application.video.create.CreateVideoOutput;
import com.fullcycle.admin.catalogo.application.video.create.CreateVideoUseCase;
import com.fullcycle.admin.catalogo.application.video.delete.DeleteVideoUseCase;
import com.fullcycle.admin.catalogo.application.video.media.get.GetMediaUseCase;
import com.fullcycle.admin.catalogo.application.video.media.get.MediaOutput;
import com.fullcycle.admin.catalogo.application.video.media.upload.UploadMediaOutput;
import com.fullcycle.admin.catalogo.application.video.media.upload.UploadMediaUseCase;
import com.fullcycle.admin.catalogo.application.video.retrieve.get.GetVideoByIdUseCase;
import com.fullcycle.admin.catalogo.application.video.retrieve.get.VideoOutput;
import com.fullcycle.admin.catalogo.application.video.retrieve.list.ListVideosUseCase;
import com.fullcycle.admin.catalogo.application.video.retrieve.list.VideoListOutput;
import com.fullcycle.admin.catalogo.application.video.update.UpdateVideoCommand;
import com.fullcycle.admin.catalogo.application.video.update.UpdateVideoOutput;
import com.fullcycle.admin.catalogo.application.video.update.UpdateVideoUseCase;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.resource.Resource;
import com.fullcycle.admin.catalogo.domain.utils.CollectionUtils;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.video.*;
import com.fullcycle.admin.catalogo.infrastructure.video.models.CreateVideoRequest;
import com.fullcycle.admin.catalogo.infrastructure.video.models.UpdateVideoRequest;
import io.vavr.API;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.testcontainers.shaded.org.hamcrest.Matchers.any;
import static org.testcontainers.shaded.org.hamcrest.Matchers.equalTo;

@ControllerTest(controllers = VideoAPI.class)
public class VideoAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateVideoUseCase createVideoUseCase;

    @MockBean
    private GetVideoByIdUseCase getVideoByIdUseCase;

    @MockBean
    private UpdateVideoUseCase updateVideoUseCase;

    @MockBean
    private DeleteVideoUseCase deleteVideoUseCase;

    @MockBean
    private ListVideosUseCase listVideosUseCase;

    @MockBean
    private GetMediaUseCase getMediaUseCase;

    @MockBean
    private UploadMediaUseCase uploadMediaUseCase;


    @Test
    public void givenAValidCommand_whenCallsCreateFull_shouldReturnAnId() throws Exception {

        final var wesley = CastMember.newMember("Wesley", CastMemberType.ACTOR);
        final var aulas = Category.newCategory("Aulas", "Aulinhas", true);
        final var tech = Genre.newGenre("Tech", true);

        final var expectedId = VideoID.unique();
        final var expectedTitle = "Title";
        final var expectedDescription = "Desc";
        final var expectedLaunchYear = Year.of(2023);
        final var expectedDuration = 105.40;
        final var expectedOpened = true;
        final var expectedPublished = true;
        final var expectedRating = Rating.AGE_10;
        final var expectedCategories = Set.of(aulas.getId().getValue());
        final var expectedGenres = Set.of(tech.getId().getValue());
        final var expectedMembers = Set.of(wesley.getId().getValue());

        final var expectedVideo = new MockMultipartFile("video_file", "video.mp4", "video/mp4", "VIDEO".getBytes());

        final var expectedTrailer = new MockMultipartFile("trailer_file", "trailer.mp4", "trailer/mp4", "TRAILER".getBytes());
        final var expectedBanner = new MockMultipartFile("banner_file", "banner.jpg", "image/jpg", "BANNER".getBytes());
        final var expectedThumb = new MockMultipartFile("thumb_file", "thumbnail.mp4", "image/jpg", "THUMB".getBytes());
        final var expectedThumbHalf = new MockMultipartFile("thumb_half_file", "thumbnailHalf.mp4", "image/jpg", "THUMBHALF".getBytes());

        Mockito.when(createVideoUseCase.execute(Mockito.any())).thenReturn(new CreateVideoOutput(expectedId.getValue()));

        final var aRequest = multipart("/videos")
                .file(expectedVideo)
                .file(expectedTrailer)
                .file(expectedBanner)
                .file(expectedThumb)
                .file(expectedThumbHalf)
                .param("title", expectedTitle)
                .param("description", expectedDescription)
                .param("year_launched", String.valueOf(expectedLaunchYear.getValue()))
                .param("duration", String.valueOf(expectedDuration))
                .param("opened", String.valueOf(expectedPublished))
                .param("published", String.valueOf(expectedOpened))
                .param("rating", expectedRating.getName())
                .param("cast_members_id", wesley.getId().getValue())
                .param("categories_id", aulas.getId().getValue())
                .param("genres_id", tech.getId().getValue())
                .param("duration", String.valueOf(expectedDuration))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        this.mvc.perform(aRequest).andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", "/videos/" + expectedId.getValue()))
                .andExpect(MockMvcResultMatchers.header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(expectedId.getValue())));


        final var cmdCaptor = ArgumentCaptor.forClass(CreateVideoCommand.class);

        Mockito.verify(createVideoUseCase).execute(cmdCaptor.capture());

        final var actualVideo = cmdCaptor.getValue();

        Assertions.assertEquals(expectedTitle, actualVideo.title());
        Assertions.assertEquals(expectedDescription, actualVideo.description());
        Assertions.assertEquals(expectedLaunchYear.getValue(), actualVideo.launchedAt());
        Assertions.assertEquals(expectedDuration, actualVideo.duration());
        Assertions.assertEquals(expectedOpened, actualVideo.opened());
        Assertions.assertEquals(expectedPublished, actualVideo.published());
        Assertions.assertEquals(expectedRating.getName(), actualVideo.rating());
        Assertions.assertEquals(expectedCategories, actualVideo.categories());
        Assertions.assertEquals(expectedGenres, actualVideo.genres());
        Assertions.assertEquals(expectedVideo.getOriginalFilename(), actualVideo.getVideo().get().getName());
        Assertions.assertEquals(expectedTrailer.getOriginalFilename(), actualVideo.getTrailer().get().getName());
        Assertions.assertEquals(expectedBanner.getOriginalFilename(), actualVideo.getBanner().get().getName());
        Assertions.assertEquals(expectedThumb.getOriginalFilename(), actualVideo.getThumbnail().get().getName());
        Assertions.assertEquals(expectedThumbHalf.getOriginalFilename(), actualVideo.getThumbnailHalf().get().getName());


    }

    @Test
    public void givenAnInvalidCommand_whenCallsCreateFull_shouldReturnError() throws Exception {

        final var expectedErrorMessage = "title is required";

        Mockito.when(createVideoUseCase.execute(Mockito.any()))
                .thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        final var aRequest = multipart("/videos")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        this.mvc.perform(aRequest).andExpect(status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.equalTo(expectedErrorMessage)));

    }

    @Test
    public void givenAValidCommand_whenCallsCreatePartial_shouldReturnId() throws Exception {

        final var wesley = CastMember.newMember("Wesley", CastMemberType.ACTOR);
        final var aulas = Category.newCategory("Aulas", "Aulinhas", true);
        final var tech = Genre.newGenre("Tech", true);

        final var expectedId = VideoID.unique();
        final var expectedTitle = "Title";
        final var expectedDescription = "Desc";
        final var expectedLaunchYear = Year.of(2023);
        final var expectedDuration = 105.40;
        final var expectedOpened = true;
        final var expectedPublished = true;
        final var expectedRating = Rating.AGE_10;
        final var expectedCategories = Set.of(aulas.getId().getValue());
        final var expectedGenres = Set.of(tech.getId().getValue());
        final var expectedMembers = Set.of(wesley.getId().getValue());

        final var aCmd = new CreateVideoRequest(
                expectedTitle,
                expectedDescription,
                expectedDuration,
                expectedLaunchYear.getValue(),
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                expectedMembers,
                expectedCategories,
                expectedGenres
        );

        Mockito.when(createVideoUseCase.execute(Mockito.any())).thenReturn(new CreateVideoOutput(expectedId.getValue()));

        final var aRequest = MockMvcRequestBuilders.post("/videos")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCmd));

        this.mvc.perform(aRequest).andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", "/videos/" + expectedId.getValue()))
                .andExpect(MockMvcResultMatchers.header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(expectedId.getValue())));


        final var cmdCaptor = ArgumentCaptor.forClass(CreateVideoCommand.class);

        Mockito.verify(createVideoUseCase).execute(cmdCaptor.capture());

        final var actualVideo = cmdCaptor.getValue();

        Assertions.assertEquals(expectedTitle, actualVideo.title());
        Assertions.assertEquals(expectedDescription, actualVideo.description());
        Assertions.assertEquals(expectedLaunchYear.getValue(), actualVideo.launchedAt());
        Assertions.assertEquals(expectedDuration, actualVideo.duration());
        Assertions.assertEquals(expectedOpened, actualVideo.opened());
        Assertions.assertEquals(expectedPublished, actualVideo.published());
        Assertions.assertEquals(expectedRating.getName(), actualVideo.rating());
        Assertions.assertEquals(expectedCategories, actualVideo.categories());
        Assertions.assertEquals(expectedGenres, actualVideo.genres());
    }

    @Test
    public void givenAnInvalidCommand_whenCallsCreatePartial_shouldReturnError() throws Exception {

        final var expectedErrorMessage = "title is required";

        Mockito.when(createVideoUseCase.execute(Mockito.any())).thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        final var aRequest = MockMvcRequestBuilders.post("/videos")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "Olá Mundo!"
                        }
                        """);

        this.mvc.perform(aRequest).andExpect(status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.equalTo(expectedErrorMessage)));

    }

    @Test
    public void givenAnEmptyBody_whenCallsCreatePartial_shouldReturnError() throws Exception {

        final var expectedErrorMessage = "title is required";

        final var aRequest = MockMvcRequestBuilders.post("/videos")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(aRequest).andExpect(status().isBadRequest());

    }

    @Test
    public void givenAValidIdWhenCallsGetById_shouldReturnVideo() throws Exception {

        final var wesley = CastMember.newMember("Wesley", CastMemberType.ACTOR);
        final var aulas = Category.newCategory("Aulas", "Aulinhas", true);
        final var tech = Genre.newGenre("Tech", true);

        final var expectedTitle = "Title";
        final var expectedDescription = "Desc";
        final var expectedLaunchYear = Year.of(2023);
        final var expectedDuration = 105.40;
        final var expectedOpened = true;
        final var expectedPublished = true;
        final var expectedRating = Rating.AGE_10;
        final var expectedCategories = Set.of(aulas.getId().getValue());
        final var expectedGenres = Set.of(tech.getId().getValue());
        final var expectedMembers = Set.of(wesley.getId().getValue());

        final var expectedVideo = AudioVideoMedia.with("abc", "Video.mp4", "/123/videos");
        final var expectedTrailer = AudioVideoMedia.with("eee", "Trailer.mp4", "/123/trailers");
        final var expectedBanner = ImageMedia.with("ere", "Banner.jpg", "/123/banners");
        final var expectedThumb = ImageMedia.with("tre", "Thumb.jpg", "/123/thumbs");
        final var expectedThumbHalf = ImageMedia.with("rwe", "ThumbHalf.jpg", "/123/thumbhalfs");

        final var aVideo = Video.newVideo(
                        expectedTitle,
                        expectedDescription,
                        expectedLaunchYear,
                        expectedDuration,
                        expectedOpened,
                        expectedPublished,
                        expectedRating,
                        CollectionUtils.mapTo(expectedCategories, CategoryID::from),
                        CollectionUtils.mapTo(expectedGenres, GenreID::from),
                        CollectionUtils.mapTo(expectedMembers, CastMemberID::from)
                )
                .updateVideoMedia(expectedVideo)
                .updateTrailerMedia(expectedTrailer)
                .updateBannerMedia(expectedBanner)
                .updateThumbnailMedia(expectedThumb)
                .updateThumbnailHalfMedia(expectedThumbHalf);

        final var expectedId = aVideo.getId().getValue();

        Mockito.when(getVideoByIdUseCase.execute(Mockito.any())).thenReturn(VideoOutput.from(aVideo));

        final var aRequest = MockMvcRequestBuilders.get("/videos/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);

        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(expectedId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.equalTo(expectedTitle)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.equalTo(expectedDescription)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.year_launched", Matchers.equalTo(expectedLaunchYear.getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration", Matchers.equalTo(expectedDuration)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.opened", Matchers.equalTo(expectedOpened)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.published", Matchers.equalTo(expectedPublished)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rating", Matchers.equalTo(expectedRating.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created_at", Matchers.equalTo(aVideo.getCreatedAt().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updated_at", Matchers.equalTo(aVideo.getUpdatedAt().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.banner.id", Matchers.equalTo(expectedBanner.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.banner.name", Matchers.equalTo(expectedBanner.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.banner.location", Matchers.equalTo(expectedBanner.location())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.banner.checksum", Matchers.equalTo(expectedBanner.checkSum())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.thumbnail.id", Matchers.equalTo(expectedThumb.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.thumbnail.name", Matchers.equalTo(expectedThumb.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.thumbnail.location", Matchers.equalTo(expectedThumb.location())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.thumbnail.checksum", Matchers.equalTo(expectedThumb.checkSum())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.thumbnail_half.id", Matchers.equalTo(expectedThumbHalf.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.thumbnail_half.name", Matchers.equalTo(expectedThumbHalf.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.thumbnail_half.location", Matchers.equalTo(expectedThumbHalf.location())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.video.id", Matchers.equalTo(expectedVideo.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.thumbnail_half.checksum", Matchers.equalTo(expectedThumbHalf.checkSum())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.video.name", Matchers.equalTo(expectedVideo.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.video.checksum", Matchers.equalTo(expectedVideo.checkSum())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.video.location", Matchers.equalTo(expectedVideo.rawLocation())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.video.encoded_location", Matchers.equalTo(expectedVideo.encodedLocation())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.video.status", Matchers.equalTo(expectedVideo.status().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.trailer.id", Matchers.equalTo(expectedTrailer.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.trailer.name", Matchers.equalTo(expectedTrailer.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.trailer.checksum", Matchers.equalTo(expectedTrailer.checkSum())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.trailer.location", Matchers.equalTo(expectedTrailer.rawLocation())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.trailer.encoded_location", Matchers.equalTo(expectedTrailer.encodedLocation())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categories_id", Matchers.equalTo(new ArrayList(expectedCategories))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genres_id", Matchers.equalTo(new ArrayList(expectedGenres))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cast_members_id", Matchers.equalTo(new ArrayList(expectedMembers))));

    }

    @Test
    public void givenAnInvalidIdWhenCallsGetById_shouldReturnNotFound() throws Exception {

        final var expectedId = VideoID.unique();
        final var expectedErrorMessage = "Video with ID %s was not found".formatted(expectedId.getValue());

        Mockito.when(getVideoByIdUseCase.execute(Mockito.any())).thenThrow(NotFoundException.with(Video.class, expectedId));

        final var aRequest = MockMvcRequestBuilders.get("/videos/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);

        response.andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.equalTo(expectedErrorMessage)));

    }

    @Test
    public void givenAValidCommand_whenCallsUpdateVideo_shouldReturnVideoId() throws Exception {

        final var wesley = CastMember.newMember("Wesley", CastMemberType.ACTOR);
        final var aulas = Category.newCategory("Aulas", "Aulinhas", true);
        final var tech = Genre.newGenre("Tech", true);

        final var expectedId = VideoID.unique();
        final var expectedTitle = "Title";
        final var expectedDescription = "Desc";
        final var expectedLaunchYear = Year.of(2023);
        final var expectedDuration = 105.40;
        final var expectedOpened = true;
        final var expectedPublished = true;
        final var expectedRating = Rating.AGE_10;
        final var expectedCategories = Set.of(aulas.getId().getValue());
        final var expectedGenres = Set.of(tech.getId().getValue());
        final var expectedMembers = Set.of(wesley.getId().getValue());

        final var aCmd = new UpdateVideoRequest(
                expectedTitle,
                expectedDescription,
                expectedDuration,
                expectedLaunchYear.getValue(),
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                expectedMembers,
                expectedCategories,
                expectedGenres
        );

        Mockito.when(updateVideoUseCase.execute(Mockito.any())).thenReturn(new UpdateVideoOutput(expectedId.getValue()));

        final var aRequest = MockMvcRequestBuilders.put("/videos/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCmd));

        this.mvc.perform(aRequest).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Location", "/videos/" + expectedId.getValue()))
                .andExpect(MockMvcResultMatchers.header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(expectedId.getValue())));


        final var cmdCaptor = ArgumentCaptor.forClass(UpdateVideoCommand.class);

        Mockito.verify(updateVideoUseCase).execute(cmdCaptor.capture());

        final var actualVideo = cmdCaptor.getValue();

        Assertions.assertEquals(expectedTitle, actualVideo.title());
        Assertions.assertEquals(expectedDescription, actualVideo.description());
        Assertions.assertEquals(expectedLaunchYear.getValue(), actualVideo.launchedAt());
        Assertions.assertEquals(expectedDuration, actualVideo.duration());
        Assertions.assertEquals(expectedOpened, actualVideo.opened());
        Assertions.assertEquals(expectedPublished, actualVideo.published());
        Assertions.assertEquals(expectedRating.getName(), actualVideo.rating());
        Assertions.assertEquals(expectedCategories, actualVideo.categories());
        Assertions.assertEquals(expectedGenres, actualVideo.genres());

    }

    @Test
    public void givenAnInvalidCommand_whenCallsUpdateVideo_shouldReturnNotification() throws Exception {

        final var wesley = CastMember.newMember("Wesley", CastMemberType.ACTOR);
        final var aulas = Category.newCategory("Aulas", "Aulinhas", true);
        final var tech = Genre.newGenre("Tech", true);

        final var expectedId = VideoID.unique();

        final var expectedErrorMessage = "'title' should not be empty";
        final var expectedErrorCount = 1;

        final var expectedTitle = "";

        final var expectedDescription = "Desc";
        final var expectedLaunchYear = Year.of(2023);
        final var expectedDuration = 105.40;
        final var expectedOpened = true;
        final var expectedPublished = true;
        final var expectedRating = Rating.AGE_10;
        final var expectedCategories = Set.of(aulas.getId().getValue());
        final var expectedGenres = Set.of(tech.getId().getValue());
        final var expectedMembers = Set.of(wesley.getId().getValue());

        final var aCmd = new UpdateVideoRequest(
                expectedTitle,
                expectedDescription,
                expectedDuration,
                expectedLaunchYear.getValue(),
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                expectedMembers,
                expectedCategories,
                expectedGenres
        );

        Mockito.when(updateVideoUseCase.execute(Mockito.any())).thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        final var aRequest = MockMvcRequestBuilders.put("/videos/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCmd));

        this.mvc.perform(aRequest).andExpect(status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.equalTo(expectedErrorMessage)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", Matchers.hasSize(expectedErrorCount)));


        final var cmdCaptor = ArgumentCaptor.forClass(UpdateVideoCommand.class);

        Mockito.verify(updateVideoUseCase).execute(Mockito.any());

    }

    @Test
    public void givenAValidId_whenCallsDeleteById_shouldDeleteIt() throws Exception {

        final var expectedId = VideoID.unique();

        Mockito.doNothing().when(deleteVideoUseCase).execute(Mockito.any());

        final var aRequest = MockMvcRequestBuilders.delete("/videos/{id}", expectedId.getValue());

        final var response = this.mvc.perform(aRequest);

        response.andExpect(status().isNoContent());

        Mockito.verify(deleteVideoUseCase).execute(Mockito.eq(expectedId.getValue()));
    }

    @Test
    public void givenValidParams_whenCallsListVideos_shouldReturnPagination() throws Exception {

        final var wesley = CastMember.newMember("Wesley", CastMemberType.ACTOR);
        final var aulas = Category.newCategory("Aulas", "Aulinhas", true);
        final var tech = Genre.newGenre("Tech", true);

        final var aVideo = new VideoPreview(Video.newVideo(
                "title",
                "desc",
                Year.of(2011),
                120.0,
                true,
                false,
                Rating.AGE_12,
                Set.of(aulas.getId()),
                Set.of(tech.getId()),
                Set.of(wesley.getId())
        ));

        final var expectedPage = 0;
        final var expectedPerPage = 0;
        final var expectedTerms = "Algo";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedCastMembers = "cas1";
        final var expectedGenres = "gen1";
        final var expectedCategories = "cat1";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(VideoListOutput.from(aVideo));

        Mockito.when(listVideosUseCase.execute(Mockito.any())).thenReturn(new Pagination<VideoListOutput>(
                expectedPage,
                expectedPerPage,
                expectedTotal,
                expectedItems
        ));

        final var aRequest = MockMvcRequestBuilders.get("/videos")
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", expectedSort)
                .queryParam("search", expectedTerms)
                .queryParam("cast_members_ids", expectedCastMembers)
                .queryParam("categories_ids", expectedCategories)
                .queryParam("genres_ids", expectedGenres)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);

        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page", Matchers.equalTo(expectedPage)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.per_page", Matchers.equalTo(expectedPerPage)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total", Matchers.equalTo(expectedTotal)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize(expectedItemsCount)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id", Matchers.equalTo(aVideo.id())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].title", Matchers.equalTo(aVideo.title())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].description", Matchers.equalTo(aVideo.description())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].created_at", Matchers.equalTo(aVideo.createdAt().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].updated_at", Matchers.equalTo(aVideo.updatedAt().toString())));

        final var captor = ArgumentCaptor.forClass(VideoSearchQuery.class);

        Mockito.verify(listVideosUseCase).execute(captor.capture());

        final var actualQuery = captor.getValue();

        Assertions.assertEquals(expectedPage, actualQuery.page());
        Assertions.assertEquals(expectedPerPage, actualQuery.perPage());
        Assertions.assertEquals(expectedDirection, actualQuery.direction());
        Assertions.assertEquals(expectedSort, actualQuery.sort());
        Assertions.assertEquals(expectedTerms, actualQuery.terms());
        Assertions.assertEquals(Set.of(CategoryID.from(expectedCategories)), actualQuery.categories());
        Assertions.assertEquals(Set.of(CastMemberID.from(expectedCastMembers)), actualQuery.castMembers());
        Assertions.assertEquals(Set.of(GenreID.from(expectedGenres)), actualQuery.genres());

    }

    @Test
    public void givenEmptyParams_whenCallsListVideosWithDefaultValues_shouldReturnPagination() throws Exception {

        final var wesley = CastMember.newMember("Wesley", CastMemberType.ACTOR);
        final var aulas = Category.newCategory("Aulas", "Aulinhas", true);
        final var tech = Genre.newGenre("Tech", true);

        final var aVideo = new VideoPreview(Video.newVideo(
                "title",
                "desc",
                Year.of(2011),
                120.0,
                true,
                false,
                Rating.AGE_12,
                Set.of(aulas.getId()),
                Set.of(tech.getId()),
                Set.of(wesley.getId())
        ));

        final var expectedPage = 0;
        final var expectedPerPage = 25;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedCastMembers = "";
        final var expectedGenres = "";
        final var expectedCategories = "";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(VideoListOutput.from(aVideo));

        Mockito.when(listVideosUseCase.execute(Mockito.any())).thenReturn(new Pagination<VideoListOutput>(
                expectedPage,
                expectedPerPage,
                expectedTotal,
                expectedItems
        ));

        final var aRequest = MockMvcRequestBuilders.get("/videos")
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);

        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page", Matchers.equalTo(expectedPage)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.per_page", Matchers.equalTo(expectedPerPage)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total", Matchers.equalTo(expectedTotal)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize(expectedItemsCount)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id", Matchers.equalTo(aVideo.id())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].title", Matchers.equalTo(aVideo.title())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].description", Matchers.equalTo(aVideo.description())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].created_at", Matchers.equalTo(aVideo.createdAt().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].updated_at", Matchers.equalTo(aVideo.updatedAt().toString())));

        final var captor = ArgumentCaptor.forClass(VideoSearchQuery.class);

        Mockito.verify(listVideosUseCase).execute(captor.capture());

        final var actualQuery = captor.getValue();

        Assertions.assertEquals(expectedPage, actualQuery.page());
        Assertions.assertEquals(expectedPerPage, actualQuery.perPage());
        Assertions.assertEquals(expectedDirection, actualQuery.direction());
        Assertions.assertEquals(expectedSort, actualQuery.sort());
        Assertions.assertEquals(expectedTerms, actualQuery.terms());
        Assertions.assertTrue(actualQuery.categories().isEmpty());
        Assertions.assertTrue(actualQuery.castMembers().isEmpty());
        Assertions.assertTrue(actualQuery.genres().isEmpty());

    }

    @Test
    public void givenAValidVideoIdAndFileType_whenCallsGetMediaById_shouldReturnContent() throws Exception {

        final var expectedId = VideoID.unique();

        final String contentType = API.Match(VideoMediaType.VIDEO).of(
                API.Case(API.$(API.List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                API.Case(API.$(), "image/jpg")
        );

        final String checksum = IdUtils.uuid();
        final byte[] content = "Conteudo".getBytes();

        final var expectedMediaType = VideoMediaType.VIDEO.name().toLowerCase();
        final var expectedResource = Resource.with(content, contentType, checksum, expectedMediaType);

        final var expectedMedia = new MediaOutput(expectedResource.content(), expectedResource.contentType(), expectedMediaType);

        Mockito.when(getMediaUseCase.execute(Mockito.any())).thenReturn(expectedMedia);

        final var aRequest = MockMvcRequestBuilders.get("/videos/{id}/medias/{type}", expectedId.getValue(), VideoMediaType.VIDEO.name());

        final var response = this.mvc.perform(aRequest);

        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, expectedMedia.contentType()))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_LENGTH, String.valueOf(expectedMedia.content().length)))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s".formatted(expectedMedia.name())))
                .andExpect(MockMvcResultMatchers.content().bytes(expectedMedia.content()));
    }

    @Test
    public void givenAValidVideoIdAndFile_whenCallsUploadMedia_shouldStoreIt() throws Exception {

        final var expectedId = VideoID.unique();

        final String contentType = API.Match(VideoMediaType.VIDEO).of(
                API.Case(API.$(API.List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                API.Case(API.$(), "image/jpg")
        );

        final String checksum = IdUtils.uuid();
        final byte[] content = "Conteudo".getBytes();

        final var expectedType = VideoMediaType.VIDEO;
        final var expectedResource = Resource.with(content, contentType, checksum, expectedType.name().toLowerCase());

        final var expectedVideo = new MockMultipartFile("media_file", expectedResource.getName(), expectedResource.contentType(), expectedResource.content());

        Mockito.when(uploadMediaUseCase.execute(Mockito.any())).thenReturn(new UploadMediaOutput(expectedId.getValue(), expectedType));

        final var aRequest = multipart("/videos/{id}/medias/{type}", expectedId.getValue(), expectedType.name())
                .file(expectedVideo)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        final var response = this.mvc.perform(aRequest);

        response.andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/videos/%s/medias/%S".formatted(expectedId.getValue(), expectedType.name())))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.video_id", Matchers.equalTo(expectedId.getValue())))
                .andExpect(jsonPath("$.media_type", Matchers.equalTo(expectedType.name())));
    }

    @Test
    public void givenAnInvalidMediaType_whenCallsUploadMedia_shouldReturnError() throws Exception {

        final var expectedId = VideoID.unique();

        final String contentType = API.Match(VideoMediaType.VIDEO).of(
                API.Case(API.$(API.List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                API.Case(API.$(), "image/jpg")
        );

        final String checksum = IdUtils.uuid();
        final byte[] content = "Conteudo".getBytes();

        final var expectedType = VideoMediaType.VIDEO;
        final var expectedResource = Resource.with(content, contentType, checksum, expectedType.name().toLowerCase());

        final var expectedVideo = new MockMultipartFile("media_file", expectedResource.getName(), expectedResource.contentType(), expectedResource.content());



        final var aRequest = multipart("/videos/{id}/medias/INVALID", expectedId.getValue())
                .file(expectedVideo)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        final var response = this.mvc.perform(aRequest);

        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.equalTo("Invalid INVALID for VideoMediaType")));
    }
}
