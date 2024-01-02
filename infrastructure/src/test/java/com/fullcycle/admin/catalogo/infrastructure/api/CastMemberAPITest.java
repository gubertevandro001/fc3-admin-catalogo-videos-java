package com.fullcycle.admin.catalogo.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalogo.ControllerTest;
import com.fullcycle.admin.catalogo.application.castmember.create.CreateCastMemberOutput;
import com.fullcycle.admin.catalogo.application.castmember.create.CreateCastMemberUseCase;
import com.fullcycle.admin.catalogo.application.castmember.create.DefaultCreateCastMemberUseCase;
import com.fullcycle.admin.catalogo.application.castmember.delete.DefaultDeleteCastMemberUseCase;
import com.fullcycle.admin.catalogo.application.castmember.delete.DeleteCastMemberUseCase;
import com.fullcycle.admin.catalogo.application.castmember.retrieve.get.CastMemberOutput;
import com.fullcycle.admin.catalogo.application.castmember.retrieve.get.DefaultGetCastMemberByIdUseCase;
import com.fullcycle.admin.catalogo.application.castmember.retrieve.get.GetCastMemberByIdUseCase;
import com.fullcycle.admin.catalogo.application.castmember.retrieve.list.CastMemberListOutput;
import com.fullcycle.admin.catalogo.application.castmember.retrieve.list.DefaultListCastMembersUseCase;
import com.fullcycle.admin.catalogo.application.castmember.retrieve.list.ListCastMembersUseCase;
import com.fullcycle.admin.catalogo.application.castmember.update.DefaultUpdateCastMemberUseCase;
import com.fullcycle.admin.catalogo.application.castmember.update.UpdateCastMemberOutput;
import com.fullcycle.admin.catalogo.application.castmember.update.UpdateCastMemberUseCase;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.CreateCastMemberRequest;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.UpdateCastMemberRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest(controllers = CastMemberAPI.class)
public class CastMemberAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private DefaultCreateCastMemberUseCase createCastMemberUseCase;

    @MockBean
    private DefaultDeleteCastMemberUseCase deleteCastMemberUseCase;

    @MockBean
    private DefaultGetCastMemberByIdUseCase getCastMemberByIdUseCase;

    @MockBean
    private DefaultListCastMembersUseCase listCastMembersUseCase;

    @MockBean
    private DefaultUpdateCastMemberUseCase updateCastMemberUseCase;

    @Test
    public void givenAValidCommand_whenCallsCreateCastMember_shouldReturnItsIdentifier() throws Exception {

        final var expectedName = "Oswaldo";
        final var expectedType = CastMemberType.ACTOR;
        final var expectedId = CastMemberID.from("123");

        final var aCommand = new CreateCastMemberRequest(expectedName, expectedType);

        Mockito.when(createCastMemberUseCase.execute(Mockito.any())).thenReturn(CreateCastMemberOutput.from(expectedId));

        final var aRequest = MockMvcRequestBuilders.post("/cast_members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand));

        final var aResponse = this.mockMvc.perform(aRequest).andDo(print());

        aResponse.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(header().string("Location", "/cast_members" + expectedId.getValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(expectedId.getValue())));

        verify(createCastMemberUseCase).execute(argThat(actualCmd ->
                Objects.equals(expectedType, actualCmd.type()) &&
                Objects.equals(expectedName, actualCmd.name())));
    }

    @Test
    public void givenInvalidName_whenCallsCreateCastMember_shouldReturnNotification() throws Exception {

        final String expectedName = null;
        final var expectedType = CastMemberType.ACTOR;
        final var errorMessage = "'name' should not be null";

        final var aCommand = new CreateCastMemberRequest(null, expectedType);

        Mockito.when(createCastMemberUseCase.execute(Mockito.any())).thenThrow(NotificationException.with(new Error(errorMessage)));

        final var aRequest = MockMvcRequestBuilders.post("/cast_members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand));

        final var aResponse = this.mockMvc.perform(aRequest).andDo(print());

        aResponse.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)));

        verify(createCastMemberUseCase).execute(argThat(actualCmd ->
                Objects.equals(expectedType, actualCmd.type()) &&
                        Objects.equals(null, actualCmd.name())));
    }

    @Test
    public void givenAValidId_whenCallsGetById_shouldReturnIt() throws Exception {

        final var expectedName = "Oswaldo";
        final var expectedType = CastMemberType.ACTOR;

        final var aMember = CastMember.newMember(expectedName, expectedType);

        final var expectedId = aMember.getId().getValue();

        when(getCastMemberByIdUseCase.execute(Mockito.any()))
                .thenReturn(CastMemberOutput.from(aMember));

        final var aRequest = MockMvcRequestBuilders.get("/cast_members/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON_VALUE);

        final var response = this.mockMvc.perform(aRequest);

        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(expectedId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", equalTo(expectedName)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", equalTo(expectedType.name())));

        verify(getCastMemberByIdUseCase).execute(eq(expectedId));

    }

    @Test
    public void givenAnInValidId_whenCallsGetByIdAndCastMemberDoesntExists_shouldReturnNotFound() throws Exception {

        final var expectedErrorMessage = "CastMember with ID 123 was not found";
        final var expectedId = CastMemberID.from("123");

        when(getCastMemberByIdUseCase.execute(Mockito.any()))
                .thenThrow(NotFoundException.with(CastMember.class, expectedId));

        final var aRequest = MockMvcRequestBuilders.get("/cast_members/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON_VALUE);

        final var response = this.mockMvc.perform(aRequest);

        response.andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        verify(getCastMemberByIdUseCase).execute(eq(expectedId.getValue()));

    }

    @Test
    public void givenAValidCommand_whenCallsUpdateCastMember_shouldReturnItsIdentifier() throws Exception {

        final var expectedName = "Oswaldo";
        final var expectedType = CastMemberType.ACTOR;

        final var aMember = CastMember.newMember("Oswalda", expectedType);
        final var expectedId = aMember.getId();

        final var aCommand = new UpdateCastMemberRequest(expectedName, expectedType);

        Mockito.when(updateCastMemberUseCase.execute(Mockito.any())).thenReturn(UpdateCastMemberOutput.from(expectedId));

        final var aRequest = MockMvcRequestBuilders.put("/cast_members/{id}", expectedId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand));

        final var aResponse = this.mockMvc.perform(aRequest).andDo(print());

        aResponse.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(expectedId.getValue())));

        verify(updateCastMemberUseCase).execute(argThat(actualCmd ->
                Objects.equals(expectedId.getValue(), actualCmd.id()) &&
                Objects.equals(expectedType, actualCmd.type()) &&
                        Objects.equals(expectedName, actualCmd.name())));
    }

    @Test
    public void givenAnInvalidName_whenCallsUpdateCastMember_shouldReturnNotification() throws Exception {

        final var aMember = CastMember.newMember("Oswalda", CastMemberType.ACTOR);
        final var expectedId = aMember.getId();

        final String expectedName = null;
        final var expectedType = CastMemberType.ACTOR;
        final var errorMessage = "'name' should not be null";

        final var aCommand = new UpdateCastMemberRequest(null, expectedType);

        Mockito.when(updateCastMemberUseCase.execute(Mockito.any())).thenThrow(NotificationException.with(new Error(errorMessage)));

        final var aRequest = MockMvcRequestBuilders.put("/cast_members/{id}", expectedId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand));

        final var aResponse = this.mockMvc.perform(aRequest).andDo(print());

        aResponse.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)));

        verify(updateCastMemberUseCase).execute(argThat(actualCmd ->
                Objects.equals(expectedId.getValue(), actualCmd.id()) &&
                Objects.equals(expectedType, actualCmd.type()) &&
                        Objects.equals(null, actualCmd.name())));
    }

    @Test
    public void givenAnInvalidId_whenCallsUpdateCastMember_shouldReturnNotFound() throws Exception {

        final var expectedId = CastMemberID.from("1234");

        final var expectedName = "Oswaldo";
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorMessage = "CastMember with ID 1234 was not found";;

        final var aCommand = new UpdateCastMemberRequest(expectedName, expectedType);

        Mockito.when(updateCastMemberUseCase.execute(Mockito.any())).thenThrow(NotFoundException.with(CastMember.class, expectedId));

        final var aRequest = MockMvcRequestBuilders.put("/cast_members/{id}", expectedId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand));

        final var aResponse = this.mockMvc.perform(aRequest).andDo(print());

        aResponse.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        verify(updateCastMemberUseCase).execute(argThat(actualCmd ->
                Objects.equals(expectedId.getValue(), actualCmd.id()) &&
                        Objects.equals(expectedType, actualCmd.type()) &&
                        Objects.equals(expectedName, actualCmd.name())));
    }

    @Test
    public void givenAValidId_whenCallsDeleteById_shouldDeleteIt() throws Exception {

        final var expectedId = "123";

        doNothing().when(deleteCastMemberUseCase).execute(any());

        final var aRequest = MockMvcRequestBuilders.delete("/cast_members/{id}", expectedId);

        final var response = this.mockMvc.perform(aRequest);

        response.andExpect(status().isNoContent());

        verify(deleteCastMemberUseCase).execute(eq(expectedId));

    }

    @Test
    public void givenValidParams_whenCallsListCastMembers_shouldReturnIt() throws Exception {

        final var aMember = CastMember.newMember("Oswaldo", CastMemberType.ACTOR);

        final var expectedPage = 0;
        final var expectedPerPage = 20;
        final var expectedSort = "type";
        final var expectedTerms = "Alg";
        final var expectedDirection = "desc";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(CastMemberListOutput.from(aMember));

        when(listCastMembersUseCase.execute(any())).thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        final var aRequest = MockMvcRequestBuilders.get("/cast_members")
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("search", expectedSort)
                .queryParam("dir", expectedDirection)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mockMvc.perform(aRequest);

        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id", equalTo(aMember.getId().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].name", equalTo(aMember.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].type", equalTo(aMember.getType().name())));

        verify(listCastMembersUseCase).execute(argThat(aQuery ->
                Objects.equals(expectedPage, aQuery.page())
                && Objects.equals(expectedPerPage, aQuery.perPage())
                ));
    }

    @Test
    public void givenEmptyParams_whenCallsListCastMembers_shouldUSeDefaultAndReturnIt() throws Exception {

        final var aMember = CastMember.newMember("Oswaldo", CastMemberType.ACTOR);

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedSort = "name";
        final var expectedTerms = "";
        final var expectedDirection = "asc";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(CastMemberListOutput.from(aMember));

        when(listCastMembersUseCase.execute(any())).thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        final var aRequest = MockMvcRequestBuilders.get("/cast_members")
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mockMvc.perform(aRequest);

        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id", equalTo(aMember.getId().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].name", equalTo(aMember.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].type", equalTo(aMember.getType().name())));

        verify(listCastMembersUseCase).execute(argThat(aQuery ->
                Objects.equals(expectedPage, aQuery.page())
                        && Objects.equals(expectedPerPage, aQuery.perPage())
        ));

    }


}
