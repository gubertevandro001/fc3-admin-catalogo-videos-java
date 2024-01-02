package com.fullcycle.admin.catalogo.infrastructure.video;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.application.video.create.CreateVideoCommand;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.domain.video.*;
import com.fullcycle.admin.catalogo.infrastructure.video.persistence.VideoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@IntegrationTest
@Transactional
public class DefaultVideoGatewayTest {

    @Autowired
    private VideoGateway videoGateway;

    @Autowired
    private CastMemberGateway castMemberGateway;

    @Autowired
    private CategoryGateway categoryGateway;

    @Autowired
    private GenreGateway genreGateway;

    @Autowired
    private VideoRepository videoRepository;

    @Test
    public void testInjection() {
        Assertions.assertNotNull(videoGateway);
        Assertions.assertNotNull(castMemberGateway);
        Assertions.assertNotNull(categoryGateway);
        Assertions.assertNotNull(genreGateway);
        Assertions.assertNotNull(videoRepository);
    }

    @Test
    @Transactional
    public void givenAValidVideo_whenCallsCreate_shouldPersistIt() {

        final var wesley = castMemberGateway.create(CastMember.newMember("Wesley", CastMemberType.ACTOR));
        final var aulas = categoryGateway.create(Category.newCategory("Aulas", "Aulinhas", true));
        final var tech = genreGateway.create(Genre.newGenre("Tech", true));

        final var expectedTitle = "Title";
        final var expectedDescription = "Desc";
        final var expectedLaunchYear = Year.of(2023);
        final var expectedDuration = 105.40;
        final var expectedOpened = true;
        final var expectedPublished = true;
        final var expectedRating = Rating.AGE_10;
        final var expectedCategories = Set.of(aulas.getId());
        final var expectedGenres = Set.of(tech.getId());
        final var expectedMembers = Set.of(wesley.getId());

        final AudioVideoMedia expectedVideo = AudioVideoMedia.with("123", "video", "media/video");
        final AudioVideoMedia expectedTrailer = AudioVideoMedia.with("123", "trailer", "media/trailer");
        final ImageMedia expectedBanner = ImageMedia.with("123", "banner", "media/banner");
        final ImageMedia expectedThumb = ImageMedia.with("123", "thumb", "media/thumb");
        final ImageMedia expectedThumbHalf = ImageMedia.with("123", "thumb-half", "media/thumb-half");

        final var aVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchYear,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedMembers
        )
                .setVideo(expectedVideo)
                .setTrailer(expectedTrailer)
                .setBanner(expectedBanner)
                .setThumbnail(expectedThumb)
                .setThumbnailHalf(expectedThumbHalf);


        final var actualVideo = videoGateway.create(aVideo);

        Assertions.assertNotNull(actualVideo);
        Assertions.assertNotNull(actualVideo.getId());

        Assertions.assertEquals(expectedTitle, actualVideo.getTitle());
        Assertions.assertEquals(expectedDescription, actualVideo.getDescription());
        Assertions.assertEquals(expectedLaunchYear, actualVideo.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, actualVideo.getDuration());
        Assertions.assertEquals(expectedOpened, actualVideo.isOpened());
        Assertions.assertEquals(expectedPublished, actualVideo.isPublished());
        Assertions.assertEquals(expectedRating, actualVideo.getRating());
        Assertions.assertEquals(expectedCategories, actualVideo.getCategories());
        Assertions.assertEquals(expectedGenres, actualVideo.getGenres());
        Assertions.assertEquals(expectedVideo.name(), actualVideo.getVideo().get().name());
        Assertions.assertEquals(expectedTrailer.name(), actualVideo.getTrailer().get().name());
        Assertions.assertEquals(expectedBanner.name(), actualVideo.getBanner().get().name());
        Assertions.assertEquals(expectedThumb.name(), actualVideo.getThumbnail().get().name());
        Assertions.assertEquals(expectedThumbHalf.name(), actualVideo.getThumbnailHalf().get().name());

        final var persistedVideo = videoRepository.findById(actualVideo.getId().getValue()).get();

        Assertions.assertEquals(expectedTitle, persistedVideo.getTitle());
        Assertions.assertEquals(expectedDescription, persistedVideo.getDescription());
        Assertions.assertEquals(expectedLaunchYear, Year.of(persistedVideo.getYearLaunched()));
        Assertions.assertEquals(expectedDuration, persistedVideo.getDuration());
        Assertions.assertEquals(expectedOpened, persistedVideo.isOpened());

    }

    @Test
    @Transactional
    public void givenAValidVideo_whenCallsUpdate_shouldPersistIt() {

        final var aVideo = videoGateway.create(Video.newVideo(
                "Title",
                "desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(),
                Set.of(),
                Set.of()
        ));

        final var wesley = castMemberGateway.create(CastMember.newMember("Wesley", CastMemberType.ACTOR));
        final var aulas = categoryGateway.create(Category.newCategory("Aulas", "Aulinhas", true));
        final var tech = genreGateway.create(Genre.newGenre("Tech", true));

        final var expectedTitle = "Title1";
        final var expectedDescription = "Desc";
        final var expectedLaunchYear = Year.of(2023);
        final var expectedDuration = 105.40;
        final var expectedOpened = true;
        final var expectedPublished = true;
        final var expectedRating = Rating.AGE_10;
        final var expectedCategories = Set.of(aulas.getId());
        final var expectedGenres = Set.of(tech.getId());
        final var expectedMembers = Set.of(wesley.getId());

        final AudioVideoMedia expectedVideo = AudioVideoMedia.with("123", "video", "media/video");
        final AudioVideoMedia expectedTrailer = AudioVideoMedia.with("123", "trailer", "media/trailer");
        final ImageMedia expectedBanner = ImageMedia.with("123", "banner", "media/banner");
        final ImageMedia expectedThumb = ImageMedia.with("123", "thumb", "media/thumb");
        final ImageMedia expectedThumbHalf = ImageMedia.with("123", "thumb-half", "media/thumb-half");

        aVideo.update(expectedTitle,
                        expectedDescription,
                        expectedLaunchYear,
                        expectedDuration,
                        expectedOpened,
                        expectedPublished,
                        expectedRating,
                        expectedCategories,
                        expectedGenres,
                        expectedMembers
                )
                .setVideo(expectedVideo)
                .setTrailer(expectedTrailer)
                .setBanner(expectedBanner)
                .setThumbnail(expectedThumb)
                .setThumbnailHalf(expectedThumbHalf);


        final var actualVideo = videoGateway.update(aVideo);

        Assertions.assertNotNull(actualVideo);
        Assertions.assertNotNull(actualVideo.getId());

        Assertions.assertEquals(expectedTitle, actualVideo.getTitle());
        Assertions.assertEquals(expectedDescription, actualVideo.getDescription());
        Assertions.assertEquals(expectedLaunchYear, actualVideo.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, actualVideo.getDuration());
        Assertions.assertEquals(expectedOpened, actualVideo.isOpened());
        Assertions.assertEquals(expectedPublished, actualVideo.isPublished());
        Assertions.assertEquals(expectedRating, actualVideo.getRating());
        Assertions.assertEquals(expectedCategories, actualVideo.getCategories());
        Assertions.assertEquals(expectedGenres, actualVideo.getGenres());
        Assertions.assertEquals(expectedVideo.name(), actualVideo.getVideo().get().name());
        Assertions.assertEquals(expectedTrailer.name(), actualVideo.getTrailer().get().name());
        Assertions.assertEquals(expectedBanner.name(), actualVideo.getBanner().get().name());
        Assertions.assertEquals(expectedThumb.name(), actualVideo.getThumbnail().get().name());
        Assertions.assertEquals(expectedThumbHalf.name(), actualVideo.getThumbnailHalf().get().name());

        final var persistedVideo = videoRepository.findById(actualVideo.getId().getValue()).get();

        Assertions.assertEquals(expectedTitle, persistedVideo.getTitle());
        Assertions.assertEquals(expectedDescription, persistedVideo.getDescription());
        Assertions.assertEquals(expectedLaunchYear, Year.of(persistedVideo.getYearLaunched()));
        Assertions.assertEquals(expectedDuration, persistedVideo.getDuration());
        Assertions.assertEquals(expectedOpened, persistedVideo.isOpened());

    }

    @Test
    public void givenAValidVideoId_whenCallsDeleteById_shouldDeleteIt() {

        final var aVideo = videoGateway.create(Video.newVideo(
                "Title",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(),
                Set.of(),
                Set.of()
        ));

        Assertions.assertEquals(1, videoRepository.count());

        final var anId = aVideo.getId();

        videoGateway.deleteById(anId);

        Assertions.assertEquals(0, videoRepository.count());

    }

    @Test
    public void givenAValidVideo_whenCallsFindById_shouldReturnIt() {

        final var wesley = castMemberGateway.create(CastMember.newMember("Wesley", CastMemberType.ACTOR));
        final var aulas = categoryGateway.create(Category.newCategory("Aulas", "Aulinhas", true));
        final var tech = genreGateway.create(Genre.newGenre("Tech", true));

        final var expectedTitle = "Title";
        final var expectedDescription = "Desc";
        final var expectedLaunchYear = Year.of(2023);
        final var expectedDuration = 105.40;
        final var expectedOpened = true;
        final var expectedPublished = true;
        final var expectedRating = Rating.AGE_10;
        final var expectedCategories = Set.of(aulas.getId());
        final var expectedGenres = Set.of(tech.getId());
        final var expectedMembers = Set.of(wesley.getId());

        final AudioVideoMedia expectedVideo = AudioVideoMedia.with("123", "video", "media/video");
        final AudioVideoMedia expectedTrailer = AudioVideoMedia.with("123", "trailer", "media/trailer");
        final ImageMedia expectedBanner = ImageMedia.with("123", "banner", "media/banner");
        final ImageMedia expectedThumb = ImageMedia.with("123", "thumb", "media/thumb");
        final ImageMedia expectedThumbHalf = ImageMedia.with("123", "thumb-half", "media/thumb-half");

        final var aVideo = videoGateway.create(Video.newVideo(
                        expectedTitle,
                        expectedDescription,
                        expectedLaunchYear,
                        expectedDuration,
                        expectedOpened,
                        expectedPublished,
                        expectedRating,
                        expectedCategories,
                        expectedGenres,
                        expectedMembers
                )
                .setVideo(expectedVideo)
                .setTrailer(expectedTrailer)
                .setBanner(expectedBanner)
                .setThumbnail(expectedThumb)
                .setThumbnailHalf(expectedThumbHalf));


        final var actualVideo = videoGateway.findById(aVideo.getId()).get();

        Assertions.assertNotNull(actualVideo);
        Assertions.assertNotNull(actualVideo.getId());

        Assertions.assertEquals(expectedTitle, actualVideo.getTitle());
        Assertions.assertEquals(expectedDescription, actualVideo.getDescription());
        Assertions.assertEquals(expectedLaunchYear, actualVideo.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, actualVideo.getDuration());
        Assertions.assertEquals(expectedOpened, actualVideo.isOpened());
        Assertions.assertEquals(expectedPublished, actualVideo.isPublished());
        Assertions.assertEquals(expectedRating, actualVideo.getRating());
        Assertions.assertEquals(expectedCategories, actualVideo.getCategories());
        Assertions.assertEquals(expectedGenres, actualVideo.getGenres());
        Assertions.assertEquals(expectedVideo.name(), actualVideo.getVideo().get().name());
        Assertions.assertEquals(expectedTrailer.name(), actualVideo.getTrailer().get().name());
        Assertions.assertEquals(expectedBanner.name(), actualVideo.getBanner().get().name());
        Assertions.assertEquals(expectedThumb.name(), actualVideo.getThumbnail().get().name());
        Assertions.assertEquals(expectedThumbHalf.name(), actualVideo.getThumbnailHalf().get().name());

        final var persistedVideo = videoRepository.findById(actualVideo.getId().getValue()).get();

        Assertions.assertEquals(expectedTitle, persistedVideo.getTitle());
        Assertions.assertEquals(expectedDescription, persistedVideo.getDescription());
        Assertions.assertEquals(expectedLaunchYear, Year.of(persistedVideo.getYearLaunched()));
        Assertions.assertEquals(expectedDuration, persistedVideo.getDuration());
        Assertions.assertEquals(expectedOpened, persistedVideo.isOpened());

    }

    @Test
    public void givenEmptyParams_whenCallFindAll_shouldReturnAllList() {

        final var wesley = castMemberGateway.create(CastMember.newMember("Wesley", CastMemberType.ACTOR));
        final var gabriel = castMemberGateway.create(CastMember.newMember("Gabriel", CastMemberType.ACTOR));

        final var aulas = categoryGateway.create(Category.newCategory("Aulas", "Aulinhas", true));
        final var lives = categoryGateway.create(Category.newCategory("Lives", "Livezinha", true));

        final var tech = genreGateway.create(Genre.newGenre("Tech", true));
        final var mob = genreGateway.create(Genre.newGenre("Mob", true));

        videoGateway.create(Video.newVideo(
                "System Design",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(lives.getId()),
                Set.of(tech.getId()),
                Set.of(wesley.getId())
        ));

        videoGateway.create(Video.newVideo(
                "Patterns in Java",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(),
                Set.of(),
                Set.of()
        ));

        videoGateway.create(Video.newVideo(
                "Domain Driven Design",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(aulas.getId()),
                Set.of(tech.getId()),
                Set.of(gabriel.getId())
        ));

        videoGateway.create(Video.newVideo(
                "Marx Angels",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(aulas.getId()),
                Set.of(mob.getId()),
                Set.of(gabriel.getId())
        ));

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTotal = 4;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of());

        final var actualPage = videoGateway.findAll(aQuery);

        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedTotal, actualPage.items().size());

    }

    @Test
    public void givenEmptyVideos_whenCallFindAll_shouldReturnEmptyList() {

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTotal = 0;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of());

        final var actualPage = videoGateway.findAll(aQuery);

        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
    }

    @Test
    public void givenAValidCategory_whenCallFindAll_shouldReturnFilteredList() {

        final var wesley = castMemberGateway.create(CastMember.newMember("Wesley", CastMemberType.ACTOR));
        final var gabriel = castMemberGateway.create(CastMember.newMember("Gabriel", CastMemberType.ACTOR));

        final var aulas = categoryGateway.create(Category.newCategory("Aulas", "Aulinhas", true));
        final var lives = categoryGateway.create(Category.newCategory("Lives", "Livezinha", true));

        final var tech = genreGateway.create(Genre.newGenre("Tech", true));
        final var mob = genreGateway.create(Genre.newGenre("Mob", true));

        videoGateway.create(Video.newVideo(
                "System Design",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(lives.getId()),
                Set.of(tech.getId()),
                Set.of(wesley.getId())
        ));

        videoGateway.create(Video.newVideo(
                "Patterns in Java",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(),
                Set.of(),
                Set.of()
        ));

        videoGateway.create(Video.newVideo(
                "Domain Driven Design",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(aulas.getId()),
                Set.of(tech.getId()),
                Set.of(gabriel.getId())
        ));

        videoGateway.create(Video.newVideo(
                "Marx Angels",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(aulas.getId()),
                Set.of(mob.getId()),
                Set.of(gabriel.getId())
        ));

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTotal = 2;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(aulas.getId()),
                Set.of());

        final var actualPage = videoGateway.findAll(aQuery);

        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());

        Assertions.assertEquals("Domain Driven Design", actualPage.items().get(0).title());
        Assertions.assertEquals("Marx Angels", actualPage.items().get(1).title());

    }

    @Test
    public void givenAValidCastMember_whenCallFindAll_shouldReturnFilteredList() {

        final var wesley = castMemberGateway.create(CastMember.newMember("Wesley", CastMemberType.ACTOR));
        final var gabriel = castMemberGateway.create(CastMember.newMember("Gabriel", CastMemberType.ACTOR));

        final var aulas = categoryGateway.create(Category.newCategory("Aulas", "Aulinhas", true));
        final var lives = categoryGateway.create(Category.newCategory("Lives", "Livezinha", true));

        final var tech = genreGateway.create(Genre.newGenre("Tech", true));
        final var mob = genreGateway.create(Genre.newGenre("Mob", true));

        videoGateway.create(Video.newVideo(
                "System Design",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(lives.getId()),
                Set.of(tech.getId()),
                Set.of(wesley.getId())
        ));

        videoGateway.create(Video.newVideo(
                "Patterns in Java",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(),
                Set.of(),
                Set.of()
        ));

        videoGateway.create(Video.newVideo(
                "Domain Driven Design",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(aulas.getId()),
                Set.of(tech.getId()),
                Set.of(wesley.getId())
        ));

        videoGateway.create(Video.newVideo(
                "Marx Angels",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(aulas.getId()),
                Set.of(mob.getId()),
                Set.of(gabriel.getId())
        ));

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTotal = 2;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(wesley.getId()),
                Set.of(),
                Set.of());

        final var actualPage = videoGateway.findAll(aQuery);

        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());

        Assertions.assertEquals("Domain Driven Design", actualPage.items().get(0).title());
        Assertions.assertEquals("Marx Angels", actualPage.items().get(1).title());
    }

    @Test
    public void givenAValidGenre_whenCallFindAll_shouldReturnFilteredList() {

        final var wesley = castMemberGateway.create(CastMember.newMember("Wesley", CastMemberType.ACTOR));
        final var gabriel = castMemberGateway.create(CastMember.newMember("Gabriel", CastMemberType.ACTOR));

        final var aulas = categoryGateway.create(Category.newCategory("Aulas", "Aulinhas", true));
        final var lives = categoryGateway.create(Category.newCategory("Lives", "Livezinha", true));

        final var tech = genreGateway.create(Genre.newGenre("Tech", true));
        final var mob = genreGateway.create(Genre.newGenre("Mob", true));

        videoGateway.create(Video.newVideo(
                "System Design",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(lives.getId()),
                Set.of(tech.getId()),
                Set.of(wesley.getId())
        ));

        videoGateway.create(Video.newVideo(
                "Patterns in Java",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(),
                Set.of(),
                Set.of()
        ));

        videoGateway.create(Video.newVideo(
                "Domain Driven Design",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(aulas.getId()),
                Set.of(tech.getId()),
                Set.of(wesley.getId())
        ));

        videoGateway.create(Video.newVideo(
                "Marx Angels",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(aulas.getId()),
                Set.of(mob.getId()),
                Set.of(gabriel.getId())
        ));

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTotal = 2;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of(tech.getId()));

        final var actualPage = videoGateway.findAll(aQuery);

        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());

        Assertions.assertEquals("Domain Driven Design", actualPage.items().get(0).title());
        Assertions.assertEquals("System Design", actualPage.items().get(1).title());
    }

    @Test
    public void givenAllParameters_whenCallFindAll_shouldReturnFilteredList() {
        final var wesley = castMemberGateway.create(CastMember.newMember("Wesley", CastMemberType.ACTOR));
        final var gabriel = castMemberGateway.create(CastMember.newMember("Gabriel", CastMemberType.ACTOR));

        final var aulas = categoryGateway.create(Category.newCategory("Aulas", "Aulinhas", true));
        final var lives = categoryGateway.create(Category.newCategory("Lives", "Livezinha", true));

        final var tech = genreGateway.create(Genre.newGenre("Tech", true));
        final var mob = genreGateway.create(Genre.newGenre("Mob", true));

        videoGateway.create(Video.newVideo(
                "System Design",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(lives.getId()),
                Set.of(tech.getId()),
                Set.of(wesley.getId())
        ));

        videoGateway.create(Video.newVideo(
                "Patterns in Java",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(),
                Set.of(),
                Set.of()
        ));

        videoGateway.create(Video.newVideo(
                "Domain Driven Design",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(aulas.getId()),
                Set.of(tech.getId()),
                Set.of(wesley.getId())
        ));

        videoGateway.create(Video.newVideo(
                "Marx Angels",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(aulas.getId()),
                Set.of(mob.getId()),
                Set.of(gabriel.getId())
        ));

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTotal = 1;
        final var expectedTerms = "Domain";
        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(wesley.getId()),
                Set.of(aulas.getId()),
                Set.of(tech.getId()));

        final var actualPage = videoGateway.findAll(aQuery);

        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());

        Assertions.assertEquals("Domain Driven Design", actualPage.items().get(0).title());
    }


    @ParameterizedTest
    @CsvSource({
            "title,asc,0,10,4,4,Domain Driven Design",
            "title,desc,0,10,4,4,System Design",
            "createdAt,asc,0,10,4,4,System Design",
            "createdAt,desc,0,10,4,4,Marx Angels",
    })
    public void givenAValidSortAndDirection_whenCallsFindAll_shouldReturnOrdered(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedVideo
    ) {

        final var wesley = castMemberGateway.create(CastMember.newMember("Wesley", CastMemberType.ACTOR));
        final var gabriel = castMemberGateway.create(CastMember.newMember("Gabriel", CastMemberType.ACTOR));

        final var aulas = categoryGateway.create(Category.newCategory("Aulas", "Aulinhas", true));
        final var lives = categoryGateway.create(Category.newCategory("Lives", "Livezinha", true));

        final var tech = genreGateway.create(Genre.newGenre("Tech", true));
        final var mob = genreGateway.create(Genre.newGenre("Mob", true));

        videoGateway.create(Video.newVideo(
                "System Design",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(lives.getId()),
                Set.of(tech.getId()),
                Set.of(wesley.getId())
        ));

        videoGateway.create(Video.newVideo(
                "Patterns in Java",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(),
                Set.of(),
                Set.of()
        ));

        videoGateway.create(Video.newVideo(
                "Domain Driven Design",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(aulas.getId()),
                Set.of(tech.getId()),
                Set.of(wesley.getId())
        ));

        videoGateway.create(Video.newVideo(
                "Marx Angels",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(aulas.getId()),
                Set.of(mob.getId()),
                Set.of(gabriel.getId())
        ));

        final var expectedTerms = "";

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of()
        );

        final var actualPage = videoGateway.findAll(aQuery);

        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedItemsCount, actualPage.items().size());
        Assertions.assertEquals(expectedVideo, actualPage.items().get(0).title());

    }

    @ParameterizedTest
    @CsvSource({
            "domain,0,10,1,1,Domain Driven Design",
            "system,0,10,1,1,System Design",
            "marx,0,10,1,1,Marx Angels",
            "java,0,10,1,1,Patterns in Java",
    })
    public void givenAValidTerm_whenCallsFindAll_shouldReturnFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedVideo
    ) {

        final var wesley = castMemberGateway.create(CastMember.newMember("Wesley", CastMemberType.ACTOR));
        final var gabriel = castMemberGateway.create(CastMember.newMember("Gabriel", CastMemberType.ACTOR));

        final var aulas = categoryGateway.create(Category.newCategory("Aulas", "Aulinhas", true));
        final var lives = categoryGateway.create(Category.newCategory("Lives", "Livezinha", true));

        final var tech = genreGateway.create(Genre.newGenre("Tech", true));
        final var mob = genreGateway.create(Genre.newGenre("Mob", true));

        videoGateway.create(Video.newVideo(
                "System Design",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(lives.getId()),
                Set.of(tech.getId()),
                Set.of(wesley.getId())
        ));

        videoGateway.create(Video.newVideo(
                "Patterns in Java",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(),
                Set.of(),
                Set.of()
        ));

        videoGateway.create(Video.newVideo(
                "Domain Driven Design",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(aulas.getId()),
                Set.of(tech.getId()),
                Set.of(wesley.getId())
        ));

        videoGateway.create(Video.newVideo(
                "Marx Angels",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(aulas.getId()),
                Set.of(mob.getId()),
                Set.of(gabriel.getId())
        ));


        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of());

        final var actualPage = videoGateway.findAll(aQuery);

        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedItemsCount, actualPage.items().size());
        Assertions.assertEquals(expectedVideo, actualPage.items().get(0).title());

    }

    @ParameterizedTest
    @CsvSource({
            "0,2,2,4,Domain Driven Design;Marx Angels",
            "1,2,2,4,Patterns in Java;System Design",
    })
    public void givenAValidPaging_whenCallsFindAll_shouldReturnPaged(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedVideos
    ) {

        final var wesley = castMemberGateway.create(CastMember.newMember("Wesley", CastMemberType.ACTOR));
        final var gabriel = castMemberGateway.create(CastMember.newMember("Gabriel", CastMemberType.ACTOR));

        final var aulas = categoryGateway.create(Category.newCategory("Aulas", "Aulinhas", true));
        final var lives = categoryGateway.create(Category.newCategory("Lives", "Livezinha", true));

        final var tech = genreGateway.create(Genre.newGenre("Tech", true));
        final var mob = genreGateway.create(Genre.newGenre("Mob", true));

        videoGateway.create(Video.newVideo(
                "System Design",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(lives.getId()),
                Set.of(tech.getId()),
                Set.of(wesley.getId())
        ));

        videoGateway.create(Video.newVideo(
                "Patterns in Java",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(),
                Set.of(),
                Set.of()
        ));

        videoGateway.create(Video.newVideo(
                "Domain Driven Design",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(aulas.getId()),
                Set.of(tech.getId()),
                Set.of(wesley.getId())
        ));

        videoGateway.create(Video.newVideo(
                "Marx Angels",
                "Desc",
                Year.of(2023),
                150.00,
                true,
                false,
                Rating.AGE_12,
                Set.of(aulas.getId()),
                Set.of(mob.getId()),
                Set.of(gabriel.getId())
        ));


        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of()
        );

        final var actualPage = videoGateway.findAll(aQuery);

        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedItemsCount, actualPage.items().size());

    }
}
