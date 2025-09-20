package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.integration.AbstractIntegrationTest;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.DeckRepository;
import com.example.flashcards_backend.repository.SubjectRepository;
import com.example.flashcards_backend.service.CsvExportService;
import com.example.flashcards_backend.service.CurrentUserService;
import com.example.flashcards_backend.service.DeckService;
import com.example.flashcards_backend.service.SubjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CsvExportControllerTest extends AbstractIntegrationTest {

  private static final String PATH = "/export";

  private RequestPostProcessor jwt;

  @Autowired private SubjectService subjectService;
  @Autowired private CurrentUserService currentUserService;
  @Autowired private CsvExportService csvExportService;
  @Autowired private DeckService deckService;
  @Autowired private SubjectRepository subjectRepository;
  @Autowired private DeckRepository deckRepository;
  @Autowired private CardRepository cardRepository;

  Subject subject;
  Deck deck1;
  Deck deck2;
  Card card1;
  Card card2;

  @BeforeEach
  void setUp() {
    jwt = jwtForTestUser();
    subject = Subject.builder().user(testUser).name("Subject 1").build();
    subjectRepository.saveAndFlush(subject);
    deck1 = Deck.builder().name("Deck 1").subject(subject).user(testUser).build();
    deck2 = Deck.builder().name("Deck 2").subject(subject).user(testUser).build();
    deckRepository.saveAllAndFlush(List.of(deck1, deck2));
    card1 =
        Card.builder()
            .front("Front 1")
            .back("Back 1")
            .user(testUser)
            .subject(subject)
            .build();
    card2 =
        Card.builder()
            .front("Front 2")
            .back("Back 2")
            .user(testUser)
            .subject(subject)
            .build();
    cardRepository.saveAllAndFlush(List.of(card1, card2));
    card1.addDecks(Set.of(deck1, deck2));
    card2.addDecks(Set.of(deck1));
    cardRepository.saveAllAndFlush(List.of(card1, card2));
  }

  @Test
  void exportSubjectCsv_existingSubject_exportsAllCards() throws Exception {
    mockMvc
        .perform(get(PATH + "/subject/" + subject.getId()).with(jwt))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Disposition", "attachment; filename=\"subject_Subject 1.csv\""))
        .andExpect(header().string("Content-Type", "text/csv"))
        .andExpect(content().string(
            "front,back,hint_front,hint_back,decks\n" +
            "Front 1,Back 1,,,Deck 1;Deck 2\n" +
            "Front 2,Back 2,,,Deck 1\n"
        ));
  }
}
