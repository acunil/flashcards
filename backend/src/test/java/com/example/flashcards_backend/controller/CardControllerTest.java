package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.CardRequest;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.CardCreationResult;
import com.example.flashcards_backend.model.CardHistory;
import com.example.flashcards_backend.service.CardService;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
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

    private Card c1;
    private Card c2;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper()
            // for Java record ctor parameter names
            .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
            // for java.time support
            .registerModule(new JavaTimeModule())
            // serialize dates as ISO strings, not timestamps
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        MappingJackson2HttpMessageConverter jacksonConverter =
            new MappingJackson2HttpMessageConverter(objectMapper);


        CardHistory history1 = CardHistory.builder()
            .id(1L)
            .avgRating(2.0)
            .viewCount(10)
            .lastViewed(LocalDateTime.parse("2023-10-01T12:00:00"))
            .lastRating(3)
            .build();
        c1 = Card.builder().id(1L).front("f1").back("b1").cardHistory(history1).build();
        c2 = Card.builder().id(2L).front("f2").back("b2").cardHistory(history1).build();

        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            // point at your real exception‐handler class
            .setControllerAdvice(new RestExceptionHandler())
            // register our customized ObjectMapper
            .setMessageConverters(jacksonConverter)
            .build();
    }

    @Test
    void getAll_returnsListOfCardResponse() throws Exception {
        when(cardService.getAllCards(false)).thenReturn(List.of(c1, c2));

        mockMvc.perform(get(ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].front").value("f1"))
            .andExpect(jsonPath("$[0].back").value("b1"))
            .andExpect(jsonPath("$[0].avgRating").value(2))
            .andExpect(jsonPath("$[0].viewCount").value(10))
            .andExpect(jsonPath("$[0].lastViewed").value("2023-10-01T12:00"))
            .andExpect(jsonPath("$[0].lastRating").value(3))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].front").value("f2"))
            .andExpect(jsonPath("$[1].back").value("b2"))
            .andExpect(jsonPath("$[1].avgRating").value(2))
            .andExpect(jsonPath("$[1].viewCount").value(10))
            .andExpect(jsonPath("$[1].lastViewed").value("2023-10-01T12:00"))
            .andExpect(jsonPath("$[1].lastRating").value(3));
        verify(cardService, never()).getAllCards(true);
    }

    @Test
    void getAll_shuffledTrue_returnsListOfCardResponse() throws Exception {
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
        when(cardService.getCardById(1L)).thenReturn(c1);

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
            .when(cardService).getCardById(99L);

        mockMvc.perform(get(ENDPOINT + "/99"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType( MediaType.APPLICATION_JSON))
            .andExpect(content().string("\"Card not found with id: 99\""));
    }

    @Test
    void create_Card_validDto_returnsCreatedWithLocationAndBody() throws Exception {
        Card created = Card.builder().id(10L).front("f").back("b").build();
        when(cardService.createCard(any(CardRequest.class)))
            .thenReturn(new CardCreationResult(created, false));

        String json = """
            {"front":"f","back":"b"}
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
            {"front":"newF","back":"newB","decks":null}
            """;

        mockMvc.perform(put(ENDPOINT + "/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isNoContent());

        verify(cardService).updateCard(5L, CardRequest.of("newF", "newB"));
    }

    @Test
    void update_missingId_returnsNotFound() throws Exception {
        doThrow(new CardNotFoundException(7L))
            .when(cardService).updateCard(anyLong(), any(CardRequest.class));

        String requestJson = """
            {"front":"newF","back":"newB","decks":null,"rating":null}
            """;

        mockMvc.perform(put(ENDPOINT + "/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isNotFound())
            .andExpect(content().string("\"Card not found with id: 7\""));
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
            .andExpect(content().string("\"Card not found with id: 55\""));
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
}
