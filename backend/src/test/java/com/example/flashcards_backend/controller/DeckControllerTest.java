package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.CreateDeckRequest;
import com.example.flashcards_backend.dto.DeckNamesDto;
import com.example.flashcards_backend.exception.DeckNotFoundException;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.service.CardDeckService;
import com.example.flashcards_backend.service.DeckService;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;

@WebMvcTest(DeckController.class)
class DeckControllerTest {

    public static final String ENDPOINT = "/api/decks";

    @MockitoBean
    private CardDeckService cardDeckService;

    @MockitoBean
    private DeckService deckService;

    @Autowired
    private MockMvc mockMvc;

    private Deck deck1;
    private Deck deck2;

    @BeforeEach
    void setUp() {
        deck1 = Deck.builder().id(1L).name("Deck 1").build();
        deck2 = Deck.builder().id(2L).name("Deck 2").build();
    }

    @Test
    void getAllDecks() throws Exception {
        // Mock the service to return a set of decks
        when(deckService.getAll()).thenReturn(Set.of(deck1, deck2));

        // Perform the GET request and verify the response
        mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(deck1.getId()))
                .andExpect(jsonPath("$[0].name").value(deck1.getName()))
                .andExpect(jsonPath("$[1].id").value(deck2.getId()))
                .andExpect(jsonPath("$[1].name").value(deck2.getName()));
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
        Deck newDeck = Deck.builder().id(3L).name("New Deck").build();
        when(cardDeckService.createDeck(any(CreateDeckRequest.class))).thenReturn(newDeck);

        String content = "{\"name\": \"New Deck\"}";
        mockMvc.perform(post(ENDPOINT + "/create")
                .contentType("application/json")
                .content(content))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(newDeck.getId()))
                .andExpect(jsonPath("$.name").value(newDeck.getName()));
    }

    @Test
    void updateDeckName() throws Exception {
        Deck updatedDeck = Deck.builder().id(1L).name("Updated Deck").build();
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
    void getDecksByNames() throws Exception {
        when(deckService.getDeckByName("Deck 1")).thenReturn(deck1);
        when(deckService.getDeckByName("Deck 2")).thenReturn(deck2);

        DeckNamesDto deckNamesDto = DeckNamesDto.of("Deck 1", "Deck 2");
        JsonNode json = deckNamesDto.toJson();
//        String jsonString = '{"deckNames":["Deck 1","Deck 2"]}';

        mockMvc.perform(post(ENDPOINT)
                .param("deckNamesDto", json.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(deck1.getId()))
                .andExpect(jsonPath("$[0].name").value(deck1.getName()))
                .andExpect(jsonPath("$[1].id").value(deck2.getId()))
                .andExpect(jsonPath("$[1].name").value(deck2.getName()));
    }

}