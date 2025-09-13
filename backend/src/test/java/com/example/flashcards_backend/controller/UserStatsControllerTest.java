package com.example.flashcards_backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.flashcards_backend.integration.AbstractIntegrationTest;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.CardHistory;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.CardHistoryRepository;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.SubjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

class UserStatsControllerTest extends AbstractIntegrationTest {

  private RequestPostProcessor jwt;

  @Autowired private CardRepository cardRepository;
  @Autowired private SubjectRepository subjectRepository;
  @Autowired private CardHistoryRepository cardHistoryRepository;
  @Autowired private ObjectMapper objectMapper;

  Subject subject;

  @BeforeEach
  void setUp() {
    jwt = jwtForTestUser();
    subject = Subject.builder().name("Subject 1").user(testUser).build();
    subjectRepository.saveAndFlush(subject);
    Card card1 =
        Card.builder().subject(subject).front("front1").back("back1").user(testUser).build();
    Card card2 =
        Card.builder().subject(subject).front("front2").back("back2").user(testUser).build();
    cardRepository.saveAndFlush(card1);
    cardRepository.saveAndFlush(card2);
    CardHistory ch1 =
        CardHistory.builder().user(testUser).card(card1).avgRating(1.0).viewCount(10).build();
    CardHistory ch2 =
        CardHistory.builder().user(testUser).card(card2).avgRating(3.0).viewCount(5).build();
    cardHistoryRepository.saveAndFlush(ch1);
    cardHistoryRepository.saveAndFlush(ch2);
  }

  @Test
  void testGetForUser() throws Exception {
    mockMvc
        .perform(get("/user-stats").with(jwt))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCards").value(2))
        .andExpect(jsonPath("$.mostViewedCard").exists())
        .andExpect(jsonPath("$.mostViewedCard.front").value("front1"))
        .andExpect(jsonPath("$.hardestCard").exists())
        .andExpect(jsonPath("$.hardestCard.front").value("front2"))
        .andExpect(jsonPath("$.totalCardViews").value(15));
  }

  @Test
  void testGetForUser_throwsExceptionIfUserIdNotValid() throws Exception {
    mockMvc.perform(get("/user-stats")).andExpect(status().isUnauthorized());
  }
}
