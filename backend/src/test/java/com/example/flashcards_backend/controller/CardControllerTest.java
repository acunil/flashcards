package com.example.flashcards_backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.flashcards_backend.dto.CardRequest;
import com.example.flashcards_backend.dto.HintRequest;
import com.example.flashcards_backend.dto.RateCardResponse;
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
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

class CardControllerTest extends AbstractIntegrationTest {

  public static final String ENDPOINT = "/cards";
  public static final String PAST_TIMESTAMP = "2023-10-01T12:00:00";

  private RequestPostProcessor jwt;

  @Autowired private MockMvc mockMvc;

  @Autowired private SubjectRepository subjectRepository;

  @Autowired private DeckRepository deckRepository;

  @Autowired private CardRepository cardRepository;
  @Autowired private CardHistoryRepository cardHistoryRepository;

  @Autowired private ObjectMapper objectMapper;

  private Card c1;
  private Card c2;
  private Subject subject1;

  @BeforeEach
  void setUp() {
    jwt = jwtForTestUser();
    subject1 = Subject.builder().name("Subject 1").user(testUser).build();
    subjectRepository.saveAndFlush(subject1);
    Deck deck = Deck.builder().name("Deck 1").subject(subject1).user(subject1.getUser()).build();
    deckRepository.saveAndFlush(deck);
    c1 = Card.builder().front("f1").back("b1").subject(subject1).user(testUser).build();
    c2 = Card.builder().front("f2").back("b2").subject(subject1).user(testUser).build();
    cardRepository.saveAndFlush(c1);
    cardRepository.saveAndFlush(c2);
    CardHistory cardHistory1 =
        CardHistory.builder()
            .avgRating(2.0)
            .viewCount(2)
            .lastViewed(LocalDateTime.parse(PAST_TIMESTAMP))
            .lastRating(3)
            .user(testUser)
            .build();
    cardHistory1.setCard(c1);
    cardHistoryRepository.saveAndFlush(cardHistory1);
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  void getById_existingId_returnsCardResponse() throws Exception {
    mockMvc
        .perform(get(ENDPOINT + "/" + c1.getId()).with(jwt))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.front").value("f1"))
        .andExpect(jsonPath("$.back").value("b1"));
  }

  @Test
  void getById_missingId_returnsNotFound() throws Exception {
    mockMvc
        .perform(get(ENDPOINT + "/99").with(jwt))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error").value("Card not found with id: 99"));
  }

  @Test
  void create_Card_validDto_returnsCreatedWithLocationAndBody() throws Exception {
    CardRequest request = CardRequest.of("f", "b", subject1.getId());
    String content = objectMapper.writeValueAsString(request);
    mockMvc
        .perform(post(ENDPOINT).with(jwt).contentType(MediaType.APPLICATION_JSON).content(content))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.front").value("f"))
        .andExpect(jsonPath("$.back").value("b"));
  }

  @Test
  void update_existingId_returnsNoContent() throws Exception {
    CardRequest request = CardRequest.of("newF", "newB", 1L);
    String content = objectMapper.writeValueAsString(request);
    mockMvc
        .perform(
            put(ENDPOINT + "/" + c1.getId())
                .with(jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.front").value("newF"))
        .andExpect(jsonPath("$.back").value("newB"));
  }

  @Test
  void update_missingId_returnsNotFound() throws Exception {
    String requestJson =
        """
            {"front":"newF","back":"newB","deckNamesDto":null,"subjectId":1}
            """;

    mockMvc
        .perform(
            put(ENDPOINT + "/0")
                .with(jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Card not found with id: 0"));
  }

  @Test
  void rate_validIdAndRating_updatesCardRating() throws Exception {
    MvcResult mvcResult =
        mockMvc
            .perform(
                patch(ENDPOINT + "/" + c1.getId() + "/rate")
                    .with(jwt)
                    .param("rating", "5")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    RateCardResponse rateCardResponse =
        objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(), RateCardResponse.class);
    assertThat(rateCardResponse.lastRating()).isEqualTo(5);
    assertThat(rateCardResponse.viewCount()).isEqualTo(3);
    assertThat(rateCardResponse.avgRating()).isEqualTo(3.0);
    assertThat(rateCardResponse.lastViewed()).isNotNull().doesNotContain(PAST_TIMESTAMP);
  }

  @Test
  void rate_validIdAndRating_noExistingHistory_createsCardHistory() throws Exception {
    MvcResult mvcResult =
        mockMvc
            .perform(
                patch(ENDPOINT + "/" + c2.getId() + "/rate")
                    .with(jwt)
                    .param("rating", "4")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    RateCardResponse rateCardResponse =
        objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(), RateCardResponse.class);
    assertThat(rateCardResponse.lastRating()).isEqualTo(4);
    assertThat(rateCardResponse.viewCount()).isEqualTo(1);
    assertThat(rateCardResponse.avgRating()).isEqualTo(4.0);
    assertThat(rateCardResponse.lastViewed()).isNotNull();
  }

  @Test
  void rate_missingCard_returnsNotFoundWithMessage() throws Exception {
    mockMvc
        .perform(
            patch(ENDPOINT + "/0/rate")
                .with(jwt)
                .param("rating", "2")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Card not found with id: 0"));
  }

  @Test
  void rate_invalidRatingLow_returnsBadRequest() throws Exception {
    mockMvc
        .perform(
            patch(ENDPOINT + "/" + c1.getId() + "/rate")
                .with(jwt)
                .param("rating", "0")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.error")
                .value("Validation error: rate.rating: must be greater than or equal to 1"));
  }

  @Test
  void rate_invalidRatingHigh_returnsBadRequest() throws Exception {
    mockMvc
        .perform(
            patch(ENDPOINT + "/" + c1.getId() + "/rate")
                .with(jwt)
                .param("rating", "6")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.error")
                .value("Validation error: rate.rating: must be less than or equal to 5"));
  }

  @Test
  void deleteCards_WhenIdsExist_ShouldReturnNoContent() throws Exception {
    List<Long> ids = List.of(c1.getId(), c2.getId());
    mockMvc
        .perform(
            delete(ENDPOINT)
                .with(jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteCards_WhenCardNotFound_ShouldReturnNotFound() throws Exception {
    List<Long> ids = List.of(1L, 999L);
    mockMvc
        .perform(
            delete(ENDPOINT)
                .with(jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
        .andExpect(status().isNotFound());
  }

  @Test
  void setHints_whenCardExists_updatesHints() throws Exception {
    HintRequest request = HintRequest.builder().hintFront("f").hintBack("b").build();
    String content = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            patch(ENDPOINT + "/" + c1.getId() + "/hints")
                .with(jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.hintFront").value("f"))
        .andExpect(jsonPath("$.hintBack").value("b"));
  }
}
