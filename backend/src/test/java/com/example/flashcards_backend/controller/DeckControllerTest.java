package com.example.flashcards_backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.flashcards_backend.dto.CreateDeckRequest;
import com.example.flashcards_backend.dto.DeckSummary;
import com.example.flashcards_backend.integration.AbstractIntegrationTest;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.DeckRepository;
import com.example.flashcards_backend.repository.SubjectRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

class DeckControllerTest extends AbstractIntegrationTest {

  public static final String ENDPOINT = "/decks";

  private RequestPostProcessor jwt;

  @Autowired private SubjectRepository subjectRepository;

  @Autowired private DeckRepository deckRepository;

  @Autowired private CardRepository cardRepository;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  private Deck deck1;
  private Deck deck2;
  private Subject subject1;
  private String subjectId;
  private Card card1;
  private Card card2;

  @BeforeEach
  void setUp() {
    jwt = jwtForTestUser();
    subject1 = Subject.builder().name("Subject 1").user(testUser).build();
    subjectRepository.saveAndFlush(subject1);
    deck1 = Deck.builder().name("Deck 1").subject(subject1).user(testUser).build();
    deck2 = Deck.builder().name("Deck 2").subject(subject1).user(testUser).build();
    deckRepository.save(deck1);
    deckRepository.save(deck2);
    subjectId = subject1.getId().toString();
    card1 = Card.builder().front("Front 1").back("Back 1").subject(subject1).user(testUser).build();
    card2 = Card.builder().front("Front 2").back("Back 2").subject(subject1).user(testUser).build();
    cardRepository.saveAndFlush(card1);
    cardRepository.saveAndFlush(card2);
  }

  @Test
  void getAllForSubject() throws Exception {
    ResultActions resultActions =
        mockMvc
            .perform(get(ENDPOINT).with(jwt).param("subjectId", subject1.getId().toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(deck1.getId()))
            .andExpect(jsonPath("$[0].name").value(deck1.getName()));

    String contentAsString = resultActions.andReturn().getResponse().getContentAsString();

    List<DeckSummary> deckSummaries = readSummaries(contentAsString);
    assertThat(deckSummaries)
        .hasSize(2)
        .extracting("name")
        .containsExactlyInAnyOrder("Deck 1", "Deck 2");
  }

  @Test
  void getDeckById() throws Exception {
    mockMvc
        .perform(get(ENDPOINT + "/" + deck1.getId()).with(jwt))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(deck1.getId()))
        .andExpect(jsonPath("$.name").value(deck1.getName()));
  }

  @Test
  void getDeckById_NotFound() throws Exception {
    mockMvc
        .perform(get(ENDPOINT + "/999").with(jwt))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Deck not found with id: 999"));
  }

  @Test
  void createDeck() throws Exception {
    CreateDeckRequest request =
        CreateDeckRequest.of(subject1.getId(), "New Deck", card1.getId(), card2.getId());
    String content = objectMapper.writeValueAsString(request);
    mockMvc
        .perform(
            post(ENDPOINT + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(jwt))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.name").value("New Deck"));
    cardRepository.flush();
    assertThat(card1.getDecks()).singleElement().extracting("name").isEqualTo("New Deck");
    assertThat(card2.getDecks()).singleElement().extracting("name").isEqualTo("New Deck");
  }

  @Test
  void createDeck_Conflict() throws Exception {
    CreateDeckRequest request = CreateDeckRequest.of(subject1.getId(), "Deck 1");
    String content = objectMapper.writeValueAsString(request);
    mockMvc
        .perform(
            post(ENDPOINT + "/create").contentType("application/json").content(content).with(jwt))
        .andExpect(status().isConflict())
        .andExpect(
            jsonPath("$.error")
                .value("A deck with the name 'Deck 1' already exists in subject Subject 1"));
  }

  @Test
  void updateDeckName() throws Exception {
    String content = "{\"newName\": \"Updated Deck\"}";
    mockMvc
        .perform(
            put(ENDPOINT + "/" + deck1.getId())
                .contentType("application/json")
                .content(content)
                .with(jwt))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(deck1.getId()))
        .andExpect(jsonPath("$.name").value("Updated Deck"));
  }

  @Test
  void updateDeckName_NotFound() throws Exception {
    String content = "{\"newName\": \"Updated Deck\"}";
    mockMvc
        .perform(put(ENDPOINT + "/999").contentType("application/json").content(content).with(jwt))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Deck not found with id: 999"));
  }

  @Test
  void deleteDeck() throws Exception {
    mockMvc
        .perform(delete(ENDPOINT + "/" + deck1.getId()).with(jwt))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteDeck_NotFound() throws Exception {
    mockMvc
        .perform(delete(ENDPOINT + "/999").with(jwt))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Deck not found with id: 999"));
  }

  @Test
  void upsertDecksByNamesAndSubjectIdNull_returns400() throws Exception {

    String content = "[\"Deck 1\", \"Deck 2\"]";

    mockMvc
        .perform(post(ENDPOINT).content(content).with(jwt).contentType("application/json"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResponse().getErrorMessage())
                    .isEqualTo("Required parameter 'subjectId' is not present."));
  }

  @Test
  void upsertDecksByNamesAndSubjectId() throws Exception {

    String content = "[\"Deck 1\", \"Deck 2\"]";

    String json =
        mockMvc
            .perform(
                post(ENDPOINT)
                    .with(jwt)
                    .content(content)
                    .contentType("application/json")
                    .param("subjectId", subjectId))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    List<DeckSummary> summaries = readSummaries(json);

    assertThat(summaries)
        .extracting("id", "name")
        .containsExactlyInAnyOrder(
            tuple(deck1.getId(), deck1.getName()), tuple(deck2.getId(), deck2.getName()));
  }

  @Test
  void addCardsToDeck() throws Exception {
    Set<Long> cardIds = Set.of(1L, 2L);
    mockMvc
        .perform(
            patch(ENDPOINT + "/" + deck1.getId() + "/add-cards")
                .with(jwt)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(cardIds)))
        .andExpect(status().isNoContent());
  }

  @Test
  void removeCardsFromDeck() throws Exception {
    Set<Long> cardIds = Set.of(1L, 2L);
    mockMvc
        .perform(
            patch(ENDPOINT + "/" + deck1.getId() + "/remove-cards")
                .with(jwt)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(cardIds)))
        .andExpect(status().isNoContent());
  }

  /* Helpers */

  private List<DeckSummary> readSummaries(String json) throws JsonProcessingException {
    return objectMapper.readValue(json, new TypeReference<>() {});
  }
}
