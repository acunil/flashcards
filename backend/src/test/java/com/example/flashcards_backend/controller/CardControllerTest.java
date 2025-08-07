package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.CardRequest;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.CardCreationResult;
import com.example.flashcards_backend.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
class CardControllerTest {

    public static final String ENDPOINT = "/api/cards";
    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(controller)
            .build();
    }

    @Test
    void getAll_returnsListOfCardResponse() throws Exception {
        Card c1 = Card.builder().id(1L).front("f1").back("b1").build();
        Card c2 = Card.builder().id(2L).front("f2").back("b2").build();
        when(cardService.getAllCards(false)).thenReturn(List.of(c1, c2));

        mockMvc.perform(get(ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].front").value("f1"))
            .andExpect(jsonPath("$[0].back").value("b1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].front").value("f2"))
            .andExpect(jsonPath("$[1].back").value("b2"));
        verify(cardService, never()).getAllCards(true);
    }

    @Test
    void getAll_shuffledTrue_returnsListOfCardResponse() throws Exception {
        Card c1 = Card.builder().id(1L).front("f1").back("b1").build();
        Card c2 = Card.builder().id(2L).front("f2").back("b2").build();
        when(cardService.getAllCards(true)).thenReturn(List.of(c1, c2));

        mockMvc.perform(get(ENDPOINT)
                .param("shuffled", "true"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].front").value("f1"))
            .andExpect(jsonPath("$[0].back").value("b1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].front").value("f2"))
            .andExpect(jsonPath("$[1].back").value("b2"));
        verify(cardService, never()).getAllCards(false);
    }

    @Test
    void getById_existingId_returnsCardResponse() throws Exception {
        Card c = Card.builder().id(1L).front("front").back("back").build();
        when(cardService.getCardById(1L)).thenReturn(c);

        mockMvc.perform(get(ENDPOINT + "/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.front").value("front"))
            .andExpect(jsonPath("$.back").value("back"));
    }

    @Test
    void getById_missingId_returnsNotFound() throws Exception {
        when(cardService.getCardById(99L)).thenThrow(new CardNotFoundException(99L));

        mockMvc.perform(get(ENDPOINT + "/99"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Card not found with id: 99"));
    }

    @Test
    void create_Card_validDto_returnsCreatedWithLocationAndBody() throws Exception {
        Card created = Card.builder().id(10L).front("f").back("b").build();
        when(cardService.createCard(any(CardRequest.class)))
            .thenReturn(new CardCreationResult(created, false));

        String json = """
            {"id":null,"front":"f","back":"b"}
            """;

        mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/cards/10"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.front").value("f"))
            .andExpect(jsonPath("$.back").value("b"));
    }

    @Test
    void update_existingId_returnsNoContent() throws Exception {
        String json = """
            {"id":null,"front":"newF","back":"newB","decks":null}
            """;

        mockMvc.perform(put(ENDPOINT + "/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isNoContent());

        verify(cardService).updateCard(5L, new CardRequest("newF", "newB", null));
    }

    @Test
    void update_missingId_returnsNotFound() throws Exception {
        doThrow(new CardNotFoundException(7L)).when(cardService).updateCard(eq(7L), any());

        String json = """
            {"id":null,"front":"x","back":"y"}
            """;

        mockMvc.perform(put(ENDPOINT + "/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Card not found with id: 7"));
    }

    @Test
    void rate_validIdAndRating_returnsNoContent() throws Exception {
        mockMvc.perform(put(ENDPOINT + "/7/rate")
                .param("rating", "5")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
        verify(cardService).rateCard(7L, 5);
    }

    @Test
    void rate_missingCard_returnsNotFoundWithMessage() throws Exception {
        doThrow(new CardNotFoundException(55L))
            .when(cardService).rateCard(55L, 2);
        mockMvc.perform(put(ENDPOINT + "/55/rate")
                .param("rating", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Card not found with id: 55"));
    }

    @Test
    void getByMinAvgRating_validThreshold_returnsListOfCardResponse() throws Exception {
        Card c1 = Card.builder().id(1L).front("f1").back("b1").build();
        Card c2 = Card.builder().id(2L).front("f2").back("b2").build();
        when(cardService.getCardsByMinAvgRating(3.0, false))
            .thenReturn(List.of(c1, c2));
        mockMvc.perform(get(ENDPOINT + "/minAvgRating")
                .param("threshold", "3.0"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].front").value("f1"))
            .andExpect(jsonPath("$[0].back").value("b1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].front").value("f2"))
            .andExpect(jsonPath("$[1].back").value("b2"));
        verify(cardService, never()).getCardsByMinAvgRating(3.0, true);
    }

    @Test
    void getByMinAvgRating_validThreshold_shuffledTrue_returnsListOfCardResponse() throws Exception {
        Card c1 = Card.builder().id(1L).front("f1").back("b1").build();
        Card c2 = Card.builder().id(2L).front("f2").back("b2").build();
        when(cardService.getCardsByMinAvgRating(3.0, true))
            .thenReturn(List.of(c1, c2));
        mockMvc.perform(get(ENDPOINT + "/minAvgRating")
                .param("threshold", "3.0")
                .param("shuffled", "true"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].front").value("f1"))
            .andExpect(jsonPath("$[0].back").value("b1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].front").value("f2"))
            .andExpect(jsonPath("$[1].back").value("b2"));
        verify(cardService, never()).getCardsByMinAvgRating(3.0, false);
    }

    @Test
    void getByMaxAvgRating_validThreshold_returnsListOfCardResponse() throws Exception {
        Card c1 = Card.builder().id(1L).front("f1").back("b1").build();
        Card c2 = Card.builder().id(2L).front("f2").back("b2").build();
        when(cardService.getCardsByMaxAvgRating(3.0, false))
            .thenReturn(List.of(c1, c2));
        mockMvc.perform(get(ENDPOINT + "/maxAvgRating")
                .param("threshold", "3.0"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].front").value("f1"))
            .andExpect(jsonPath("$[0].back").value("b1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].front").value("f2"))
            .andExpect(jsonPath("$[1].back").value("b2"));
        verify(cardService, never()).getCardsByMaxAvgRating(3.0, true);
    }

    @Test
    void getByMaxAvgRating_validThreshold_shuffledTrue_returnsListOfCardResponse() throws Exception {
        Card c1 = Card.builder().id(1L).front("f1").back("b1").build();
        Card c2 = Card.builder().id(2L).front("f2").back("b2").build();
        when(cardService.getCardsByMaxAvgRating(3.0, true))
            .thenReturn(List.of(c1, c2));
        mockMvc.perform(get(ENDPOINT + "/maxAvgRating")
                .param("threshold", "3.0")
                .param("shuffled", "true"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].front").value("f1"))
            .andExpect(jsonPath("$[0].back").value("b1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].front").value("f2"))
            .andExpect(jsonPath("$[1].back").value("b2"));
        verify(cardService, never()).getCardsByMaxAvgRating(3.0, false);
    }
}
