package com.fullcycle.admin.catalogo.e2e;

import com.fullcycle.admin.catalogo.domain.Identifier;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.CastMemberResponse;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.CreateCastMemberRequest;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.UpdateCastMemberRequest;
import com.fullcycle.admin.catalogo.infrastructure.category.models.CategoryResponse;
import com.fullcycle.admin.catalogo.infrastructure.category.models.CreateCategoryRequest;
import com.fullcycle.admin.catalogo.infrastructure.category.models.UpdateCategoryRequest;
import com.fullcycle.admin.catalogo.infrastructure.configuration.json.Json;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.CreateGenreRequest;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.GenreResponse;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.xml.transform.Result;
import java.util.List;
import java.util.function.Function;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public interface MockDsl {

    MockMvc mvc();

    default ResultActions listCategories(final int page, final int perPage, final String search) throws Exception {
        return listCategories(page, perPage, search, "", "");
    }

    default ResultActions listCategories(final int page, final int perPage, final String search, final String sort, final String direction) throws Exception {
        return this.list("/categories", page, perPage, search, sort, direction);
    }

    default ResultActions listCategories(final int page, final int perPage) throws Exception {
        return listCategories(page, perPage, "", "", "");
    }

    default CategoryResponse retrieveACategory(final Identifier anId) throws Exception {
        return this.retrieve("/categories/", anId, CategoryResponse.class);
    }

    default ResultActions updateACategory(final Identifier anId, final UpdateCategoryRequest aRequest) throws Exception {
        return this.update("/categories/", anId, aRequest);
    }

    default ResultActions deleteACategory(final Identifier anId) throws Exception {
        return this.delete("/categories/", anId);
    }

    default CategoryID givenACategory(final String aName, final String aDescription, final boolean isActive) throws Exception {
        final var aRequestBody = new CreateCategoryRequest(aName, aDescription, isActive);
        final var actualId = this.given("/categories", aRequestBody);
        return CategoryID.from(actualId);
    }

    default GenreID givenAGenre(final String aName, final boolean isActive, final List<CategoryID> categories) throws Exception {
        final var aRequestBody = new CreateGenreRequest(aName, mapTo(categories, CategoryID::getValue), isActive);
        final var actualId = this.given("/genres", aRequestBody);
        return GenreID.from(actualId);
    }

    default GenreResponse retrieveAGenre(final Identifier anId) throws Exception {
        return this.retrieve("/genres/", anId, GenreResponse.class);
    }

    default ResultActions updateAGenre(final Identifier anId, final UpdateGenreRequest aRequest) throws Exception {
        return this.update("/genres/", anId, aRequest);
    }

    default ResultActions deleteAGenre(final Identifier anId) throws Exception {
        return this.delete("/genres/", anId);
    }

    default <A, D> List<D> mapTo(final List<A> actual, final Function<A, D> mapper) {
        return actual.stream().map(mapper).toList();
    }

    default ResultActions listGenres(final int page, final int perPage, final String search) throws Exception {
        return listGenres(page, perPage, search, "", "");
    }

    default ResultActions listGenres(final int page, final int perPage, final String search, final String sort, final String direction) throws Exception {
        return this.list("/genres", page, perPage, search, sort, direction);
    }

    default ResultActions listGenres(final int page, final int perPage) throws Exception {
        return listGenres(page, perPage, "", "", "");
    }

    default CastMemberID givenACastMember(final String aName, final CastMemberType type) throws Exception {
        final var aRequestBody = new CreateCastMemberRequest(aName, type);
        final var actualId = this.given("/cast_members", aRequestBody);
        return CastMemberID.from(actualId);
    }

    default ResultActions givenACastMemberResult(final String aName, final CastMemberType type) throws Exception {
        final var aRequestBody = new CreateCastMemberRequest(aName, type);
        return this.givenResult("/cast_members", aRequestBody);
    }

    default ResultActions listCastMembers(final int page, final int perPage, final String search) throws Exception {
        return listCastMembers(page, perPage, search, "", "");
    }

    default ResultActions listCastMembers(final int page, final int perPage, final String search, final String sort, final String direction) throws Exception {
        return this.list("/cast_members", page, perPage, search, sort, direction);
    }

    default ResultActions listCastMembers(final int page, final int perPage) throws Exception {
        return listCastMembers(page, perPage, "", "", "");
    }

    default CastMemberResponse retrieveACastMember(final CastMemberID anId) throws Exception {
        return this.retrieve("/cast_members/", anId, CastMemberResponse.class);
    }

    default ResultActions retrieveACastMemberResult(final CastMemberID anId) throws Exception {
        return this.retrieveResult("/cast_members/", anId);
    }

    default ResultActions updateACastMember(final Identifier anId, final String aName, final CastMemberType aType) throws Exception {
        return this.update("/cast_members/", anId, new UpdateCastMemberRequest(aName, aType));
    }

    default ResultActions deleteACastMember(final Identifier anId) throws Exception {
        return this.delete("/cast_members/", anId);
    }




    private String given(final String url, final Object body) throws Exception {
        final var aRequest = MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        final var actualId = this.mvc().perform(aRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getHeader("Location")
                .replace("%s/".formatted(url), "");

        return actualId;
    }

    private ResultActions givenResult(final String url, final Object body) throws Exception {
        final var aRequest = MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        return this.mvc().perform(aRequest);

    }

    private ResultActions list(final String url, final int page, final int perPage, final String search, final String sort, final String direction) throws Exception {

        final var aRequest = MockMvcRequestBuilders.get(url)
                .queryParam("page", String.valueOf(page))
                .queryParam("perPage", String.valueOf(perPage))
                .queryParam("search", search)
                .queryParam("sort", sort)
                .queryParam("perPage", direction)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var json = this.mvc().perform(aRequest)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        return this.mvc().perform(aRequest);

    }

    private <T> T retrieve(final String url, final Identifier anId, final Class<T> clazz) throws Exception {

        final var aRequest = MockMvcRequestBuilders.get(url + anId.getValue());

        final var json = this.mvc().perform(aRequest)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        return Json.readValue(json, clazz);
    }

    private ResultActions retrieveResult(final String url, final Identifier anId) throws Exception {

        final var aRequest = MockMvcRequestBuilders.get(url + anId.getValue());

        return this.mvc().perform(aRequest);

    }

    private ResultActions delete(final String url, final Identifier anId) throws Exception {
        final var aRequest = MockMvcRequestBuilders.delete(url + anId.getValue())
                        .contentType(MediaType.APPLICATION_JSON);
        return this.mvc().perform(aRequest);
    }

    private ResultActions update(final String url, final Identifier anId, final Object aRequestBody) throws Exception {
        final var aRequest = MockMvcRequestBuilders.put(url + anId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(aRequestBody));

       return this.mvc().perform(aRequest);
    }

}
