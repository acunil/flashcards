package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.CreateDeckRequest;
import com.example.flashcards_backend.dto.DeckSummary;
import com.example.flashcards_backend.exception.DeckNotFoundException;
import com.example.flashcards_backend.exception.DuplicateDeckNameException;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.service.CardDeckService;
import com.example.flashcards_backend.service.CurrentUserService;
import com.example.flashcards_backend.service.DeckService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeckController.class)
@AutoConfigureMockMvc(addFilters = false)
class DeckControllerTest {

    public static final String ENDPOINT = "/decks";

    @MockitoBean
    private CardDeckService cardDeckService;

    @MockitoBean
    private DeckService deckService;

    @MockitoBean
    private CurrentUserService currentUserService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Deck deck1;
    private Deck deck2;
    private Subject subject1;

    @BeforeEach
    void setUp() {
        subject1 = Subject.builder().id(1L).name("Subject 1").build();
        deck1 = Deck.builder().id(1L).name("Deck 1").subject(subject1).build();
        deck2 = Deck.builder().id(2L).name("Deck 2").subject(subject1).build();
    }

    @Test
    void getAllForSubject() throws Exception {
        Subject subject2 = Subject.builder().id(2L).name("Subject 2").build();
        deck2.setSubject(subject2);

        when(deckService.getDeckSummariesBySubjectId(subject1.getId()))
                .thenReturn(Set.of(DeckSummary.fromEntity(deck1)));

        ResultActions resultActions = mockMvc.perform(get(ENDPOINT)
                        .param("subjectId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(deck1.getId()))
                .andExpect(jsonPath("$[0].name").value(deck1.getName()));

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();

        List<DeckSummary> deckSummaries = readSummaries(contentAsString);
        assertThat(deckSummaries).singleElement().isEqualTo(DeckSummary.fromEntity(deck1));
    }

    @Test
    void getDeckById() throws Exception {
        when(deckService.getDeckById(1L)).thenReturn(deck1);
        mockMvc.perform(get(ENDPOINT + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deck1.getId()))
                .andExpect(jsonPath("$.name").value(deck1.getName()));
    }

    @Test
    void getDeckById_NotFound() throws Exception {
        doThrow(new DeckNotFoundException(999L))
                .when(deckService).getDeckById(999L);
        mockMvc.perform(get(ENDPOINT + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Deck not found with id: 999"));
    }

    @Test
    void createDeck() throws Exception {
        Deck newDeck = Deck.builder().id(3L).name("New Deck").subject(subject1).build();
        when(cardDeckService.createDeck(any(CreateDeckRequest.class))).thenReturn(newDeck);

        String content = "{\"name\": \"New Deck\", \"subjectId\": 1}";
        mockMvc.perform(post(ENDPOINT + "/create")
                        .contentType("application/json")
                        .content(content))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(newDeck.getId()))
                .andExpect(jsonPath("$.name").value(newDeck.getName()));
    }

    @Test
    void createDeck_Conflict() throws Exception {
        doThrow(new DuplicateDeckNameException("Existing Deck"))
                .when(cardDeckService).createDeck(any(CreateDeckRequest.class));

        String content = "{\"name\": \"Existing Deck\", \"subjectId\": 1}";
        mockMvc.perform(post(ENDPOINT + "/create")
                        .contentType("application/json")
                        .content(content))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("A deck with the name 'Existing Deck' already exists"));
    }

    @Test
    void updateDeckName() throws Exception {
        Deck updatedDeck = Deck.builder().id(1L).name("Updated Deck").subject(subject1).build();
        when(deckService.renameDeck(1L, "Updated Deck")).thenReturn(updatedDeck);

        String content = "{\"newName\": \"Updated Deck\"}";
        mockMvc.perform(put(ENDPOINT + "/1")
                        .contentType("application/json")
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedDeck.getId()))
                .andExpect(jsonPath("$.name").value(updatedDeck.getName()));
    }

    @Test
    void updateDeckName_NotFound() throws Exception {
        doThrow(new DeckNotFoundException(999L))
                .when(deckService).renameDeck(999L, "Updated Deck");
        String content = "{\"newName\": \"Updated Deck\"}";
        mockMvc.perform(put(ENDPOINT + "/999")
                        .contentType("application/json")
                        .content(content))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Deck not found with id: 999"));
    }

    @Test
    void deleteDeck() throws Exception {
        mockMvc.perform(delete(ENDPOINT + "/1"))
                .andExpect(status().isNoContent());
        verify(deckService).deleteDeck(1L);
    }

    @Test
    void deleteDeck_NotFound() throws Exception {
        doThrow(new DeckNotFoundException(999L))
                .when(deckService).deleteDeck(999L);
        mockMvc.perform(delete(ENDPOINT + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Deck not found with id: 999"));
    }

    @Test
    void upsertDecksByNamesAndSubjectIdNull_returns400() throws Exception {
        when(cardDeckService.getOrCreateDecksByNamesAndSubjectId(Set.of("Deck 1", "Deck 2"), null))
                .thenReturn(Set.of(deck1, deck2));

        String content = "[\"Deck 1\", \"Deck 2\"]";

        mockMvc.perform(post(ENDPOINT)
                        .content(content)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResponse().getErrorMessage())
                                .isEqualTo("Required parameter 'subjectId' is not present."));
    }

    @Test
    void upsertDecksByNamesAndSubjectId() throws Exception {
        when(cardDeckService.getOrCreateDecksByNamesAndSubjectId(Set.of("Deck 1", "Deck 2"), 1L))
                .thenReturn(Set.of(deck1, deck2));

        String content = "[\"Deck 1\", \"Deck 2\"]";

        String json = mockMvc.perform(post(ENDPOINT)
                        .content(content)
                        .contentType("application/json")
                        .param("subjectId", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<DeckSummary> summaries = readSummaries(json);

        assertThat(summaries)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(
                        tuple(deck1.getId(), deck1.getName()),
                        tuple(deck2.getId(), deck2.getName())
                );
    }

    @Test
    void addCardsToDeck() throws Exception {
        Set<Long> cardIds = Set.of(1L, 2L);
        mockMvc.perform(patch(ENDPOINT + "/1/add-cards")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(cardIds)))
                .andExpect(status().isNoContent());
        verify(cardDeckService).addDeckToCards(1L, cardIds);
    }

    @Test
    void removeCardsFromDeck() throws Exception {
        Set<Long> cardIds = Set.of(1L, 2L);
        mockMvc.perform(patch(ENDPOINT + "/1/remove-cards")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(cardIds)))
                .andExpect(status().isNoContent());
        verify(cardDeckService).removeDeckFromCards(1L, cardIds);
    }


    /* Helpers */

    private List<DeckSummary> readSummaries(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, new TypeReference<>() {
        });
    }

}