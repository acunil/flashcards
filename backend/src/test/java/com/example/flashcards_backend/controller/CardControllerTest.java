package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.CardRequest;
import com.example.flashcards_backend.dto.CardResponse;
import com.example.flashcards_backend.dto.CreateCardResponse;
import com.example.flashcards_backend.dto.HintRequest;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.CardHistory;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.service.CardHistoryService;
import com.example.flashcards_backend.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(CardController.class)
class CardControllerTest {

    public static final String ENDPOINT = "/cards";

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private CardHistoryService cardHistoryService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Card c1;
    private Card c2;

    @BeforeEach
    void setUp() {
        Subject subject1 = Subject.builder().id(1L).name("Subject 1").build();
        CardHistory history1 = CardHistory.builder()
            .id(1L)
            .avgRating(2.0)
            .viewCount(10)
            .lastViewed(LocalDateTime.parse("2023-10-01T12:00:00"))
            .lastRating(3)
            .build();
        c1 = Card.builder().id(1L).front("f1").back("b1").subject(subject1).cardHistory(history1).build();
        c2 = Card.builder().id(2L).front("f2").back("b2").subject(subject1).cardHistory(history1).build();

        objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    void getById_existingId_returnsCardResponse() throws Exception {
        when(cardService.getCardResponseById(1L)).thenReturn(CardResponse.fromEntity(c1));

        mockMvc.perform(get(ENDPOINT + "/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.front").value("f1"))
            .andExpect(jsonPath("$.back").value("b1"));
    }

    @Test
    void getById_missingId_returnsNotFound() throws Exception {
        doThrow( new CardNotFoundException(99L))
            .when(cardService).getCardResponseById(99L);

        mockMvc.perform(get(ENDPOINT + "/99"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType( MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Card not found with id: 99"));
    }

    @Test
    void create_Card_validDto_returnsCreatedWithLocationAndBody() throws Exception {
        when(cardService.createCard(any(CardRequest.class)))
            .thenReturn(CreateCardResponse.builder().front("f").back("b").id(10L).build());

        String json = """
            {"front":"f","back":"b","subjectId":1}
            """;

        mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/cards/10"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.front").value("f"))
            .andExpect(jsonPath("$.back").value("b"));
    }

    @Test
    void update_existingId_returnsNoContent() throws Exception {
        String json = """
            {"front":"newF","back":"newB","deckNamesDto":null,"subjectId":1}
            """;

        mockMvc.perform(put(ENDPOINT + "/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isNoContent());

        verify(cardService).updateCard(5L, CardRequest.of("newF", "newB", 1L));
    }

    @Test
    void update_missingId_returnsNotFound() throws Exception {
        doThrow(new CardNotFoundException(7L))
            .when(cardService).updateCard(anyLong(), any(CardRequest.class));

        String requestJson = """
            {"front":"newF","back":"newB","deckNamesDto":null,"subjectId":1}
            """;

        mockMvc.perform(put(ENDPOINT + "/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath( "$.error").value("Card not found with id: 7"));
    }

    @Test
    void rate_validIdAndRating_returnsNoContent() throws Exception {
        mockMvc.perform(patch(ENDPOINT + "/7/rate")
                .param("rating", "5")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
        verify(cardHistoryService).recordRating(7L, 5);
    }

    @Test
    void rate_missingCard_returnsNotFoundWithMessage() throws Exception {
        doThrow(new CardNotFoundException(55L))
            .when(cardHistoryService).recordRating(55L, 2);
        mockMvc.perform(patch(ENDPOINT + "/55/rate")
                .param("rating", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath( "$.error").value("Card not found with id: 55"));
    }

    @Test
    void rate_databaseError_returnsInternalServerError() throws Exception {
        SQLException e = new SQLException("Test SQL error");
        DataAccessException dataAccessException = new DataAccessException("Test Database error", e) {};
        doThrow(dataAccessException)
            .when(cardHistoryService).recordRating(7L, 5);
        mockMvc.perform(patch(ENDPOINT + "/7/rate")
                .param("rating", "5")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.error")
                .value("Data access error occurred: Test Database error"));
    }

    @Test
    void getByMinAvgRating_validThreshold_returnsListOfCardResponse() throws Exception {
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

    @Test
    void deleteCards_WhenIdsExist_ShouldReturnNoContent() throws Exception {
        List<Long> ids = List.of(1L, 2L);
        doNothing().when(cardService).deleteCards(ids);

        mockMvc.perform(delete(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isNoContent());

        verify(cardService, times(1)).deleteCards(ids);
        verifyNoInteractions(cardHistoryService);
    }

    @Test
    void deleteCards_WhenCardNotFound_ShouldReturnNotFound() throws Exception {
        List<Long> ids = List.of(1L, 999L);
        doThrow(new CardNotFoundException(999L)).when(cardService).deleteCards(ids);

        mockMvc.perform(delete(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isNotFound());

        verify(cardService, times(1)).deleteCards(ids);
        verifyNoInteractions(cardHistoryService);
    }

    @Test
    void setHints_whenCardExists_updatesHints() throws Exception {
        var hintRequest = HintRequest.builder()
                .hintFront("f")
                .hintBack("b")
                .build();
        when(cardService.setHints(hintRequest, 1L)).thenReturn(CardResponse.fromEntity(c1));

        String content = """
            {"hintFront":"f","hintBack":"b"}
            """;

        mockMvc.perform(patch(ENDPOINT + "/" + 1L + "/hints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());
        verify(cardService, times(1)).setHints(hintRequest, 1L);

    }
}
