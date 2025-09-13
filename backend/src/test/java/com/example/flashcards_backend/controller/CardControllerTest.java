package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.integration.AbstractIntegrationTest;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.CardHistory;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.CardHistoryRepository;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.DeckRepository;
import com.example.flashcards_backend.repository.SubjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CardControllerTest extends AbstractIntegrationTest {

    public static final String ENDPOINT = "/cards";

  private RequestPostProcessor jwt;

    @Autowired
    private MockMvc mockMvc;

  @Autowired private SubjectRepository subjectRepository;

  @Autowired private DeckRepository deckRepository;

  @Autowired private CardRepository cardRepository;
  @Autowired private CardHistoryRepository cardHistoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Card c1;
    private Card c2;
    private Subject subject1;
    private Deck deck;
    private CardHistory cardHistory1;

    @BeforeEach
    void setUp() {
      jwt = jwtForTestUser();
        subject1 = Subject.builder().name("Subject 1").user(testUser).build();
        subjectRepository.saveAndFlush(subject1);
        deck = Deck.builder().name("Deck 1").subject(subject1).user(subject1.getUser()).build();
        deckRepository.saveAndFlush(deck);
        c1 = Card.builder().front("f1").back("b1").subject(subject1).user(testUser).build();
      c2 = Card.builder().front("f2").back("b2").subject(subject1).user(testUser).build();
      cardRepository.saveAndFlush(c1);
      cardRepository.saveAndFlush(c2);
      cardHistory1 = CardHistory.builder()
          .avgRating(2.0)
          .viewCount(10)
          .lastViewed(LocalDateTime.parse("2023-10-01T12:00:00"))
          .lastRating(3)
          .card(c1)
          .user(testUser)
          .build();
      cardHistoryRepository.saveAndFlush(cardHistory1);
      objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    void getById_existingId_returnsCardResponse() throws Exception {
        mockMvc.perform(get(ENDPOINT + "/1").with(jwt))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.front").value("f1"))
            .andExpect(jsonPath("$.back").value("b1"));
    }

    @Test
    void getById_missingId_returnsNotFound() throws Exception {
        mockMvc.perform(get(ENDPOINT + "/99").with(jwt))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType( MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Card not found with id: 99"));
    }

    @Test
    void create_Card_validDto_returnsCreatedWithLocationAndBody() throws Exception {
        String json = """
            {"front":"f","back":"b","subjectId":1}
            """;

        mockMvc.perform(post(ENDPOINT).with(jwt)
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

        mockMvc.perform(put(ENDPOINT + "/5").with(jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isNoContent());
    }

    @Test
    void update_missingId_returnsNotFound() throws Exception {
        String requestJson = """
            {"front":"newF","back":"newB","deckNamesDto":null,"subjectId":1}
            """;

        mockMvc.perform(put(ENDPOINT + "/7").with(jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath( "$.error").value("Card not found with id: 7"));
    }

    @Test
    void rate_validIdAndRating_returnsNoContent() throws Exception {
        mockMvc.perform(patch(ENDPOINT + "/7/rate").with(jwt)
                .param("rating", "5")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    void rate_missingCard_returnsNotFoundWithMessage() throws Exception {
        mockMvc.perform(patch(ENDPOINT + "/55/rate").with(jwt)
                .param("rating", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath( "$.error").value("Card not found with id: 55"));
    }

    @Test
    void rate_databaseError_returnsInternalServerError() throws Exception {
        mockMvc.perform(patch(ENDPOINT + "/7/rate").with(jwt)
                .param("rating", "5")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.error")
                .value("Data access error occurred: Test Database error"));
    }

    @Test
    void deleteCards_WhenIdsExist_ShouldReturnNoContent() throws Exception {
        List<Long> ids = List.of(1L, 2L);
        mockMvc.perform(delete(ENDPOINT).with(jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCards_WhenCardNotFound_ShouldReturnNotFound() throws Exception {
        List<Long> ids = List.of(1L, 999L);
        mockMvc.perform(delete(ENDPOINT).with(jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isNotFound());
    }

    @Test
    void setHints_whenCardExists_updatesHints() throws Exception {
        String content = """
            {"hintFront":"f","hintBack":"b"}
            """;

        mockMvc.perform(patch(ENDPOINT + "/" + 1L + "/hints").with(jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());
    }
}
