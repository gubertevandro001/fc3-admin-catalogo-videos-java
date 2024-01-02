package com.fullcycle.admin.catalogo.application;

import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.video.Rating;
import com.fullcycle.admin.catalogo.domain.resource.Resource;
import com.fullcycle.admin.catalogo.domain.video.Video;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaType;
import com.github.javafaker.Faker;
import io.vavr.API;

import java.time.Year;
import java.util.Set;

import static io.vavr.API.List;

public final class Fixture {

    private static final Faker FAKER = new Faker();

    public static String name() {
        return FAKER.name().fullName();
    }

    public static String title() {
        return FAKER.options().option(
                "System Design no Mercado Livre na prática",
                "Não cometa esses erros ao trabalhar com Microsserviços",
                "Testes de Mutação. Você não testa seu software corretamente"
        );
    }

    public static Video video() {
        return Video.newVideo(
                Fixture.title(),
                Fixture.Videos.description(),
                Year.of(Fixture.year()),
                Fixture.duration(),
                Fixture.bool(),
                Fixture.bool(),
                Videos.rating(),
                Set.of(Categories.aulas().getId()),
                Set.of(Genres.tech().getId()),
                Set.of(Fixture.CastMember.wesley().getId(), Fixture.CastMember.gabriel().getId())
        );
    }

    public static Integer year() {
        return FAKER.random().nextInt(2020, 2030);
    }

    public static Double duration() {
        return FAKER.options().option(
                120.0, 140.0, 35.5, 10.0, 2.0
        );
    }

    public static boolean bool() {
        return FAKER.bool().bool();
    }

    public static final class Categories {

        private static final Category AULAS = Category.newCategory("Aulas", "Some description", true);

        public static Category aulas() {
            return AULAS.clone();
        }
    }

    public static final class Genres {

        private static final Genre TECH = Genre.newGenre("Technology", true);

        public static Genre tech() {
            return Genre.with(TECH);
        }
    }

    public static final class CastMember {

        private static final com.fullcycle.admin.catalogo.domain.castmember.CastMember WESLEY =
                com.fullcycle.admin.catalogo.domain.castmember.CastMember.newMember("Wesley FullCycle", CastMemberType.ACTOR);

        private static final com.fullcycle.admin.catalogo.domain.castmember.CastMember GABRIEL =
                com.fullcycle.admin.catalogo.domain.castmember.CastMember.newMember("Gabriel FullCycle", CastMemberType.ACTOR);


        public static CastMemberType type() {
            return FAKER.options().option(CastMemberType.ACTOR, CastMemberType.DIRECTOR);
        }

        public static com.fullcycle.admin.catalogo.domain.castmember.CastMember wesley() {
            return com.fullcycle.admin.catalogo.domain.castmember.CastMember.with(WESLEY);
        }

        public static com.fullcycle.admin.catalogo.domain.castmember.CastMember gabriel() {
            return com.fullcycle.admin.catalogo.domain.castmember.CastMember.with(GABRIEL);
        }
    }

    public static final class Videos {

        public static Video systemDesign() {
            return Video.newVideo(
                    Fixture.title(),
                    Fixture.Videos.description(),
                    Year.of(Fixture.year()),
                    Fixture.duration(),
                    Fixture.bool(),
                    Fixture.bool(),
                    rating(),
                    Set.of(Categories.aulas().getId()),
                    Set.of(Genres.tech().getId()),
                    Set.of(Fixture.CastMember.wesley().getId(), Fixture.CastMember.gabriel().getId())
            );
        }

        public static Resource resource(final VideoMediaType type) {
            final String contentType = API.Match(type).of(
                    API.Case(API.$(List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                            API.Case(API.$(), "image/jpg")
            );

            final String checksum = IdUtils.uuid();
            final byte[] content = "Conteudo".getBytes();

            return Resource.with(content, contentType, checksum, type.name().toLowerCase());
        }

        public static Rating rating() {
            return FAKER.options().option(Rating.values());
        }

        public static String description() {
            return FAKER.options().option(
                    "Uma descrição muito simples",
                    "Uma descrição muito mais simples do que a outra"
            );
        }


    }
}
