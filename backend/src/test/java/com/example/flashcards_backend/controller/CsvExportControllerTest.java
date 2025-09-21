package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.integration.AbstractIntegrationTest;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.DeckRepository;
import com.example.flashcards_backend.service.CsvExportService;
import com.example.flashcards_backend.service.CurrentUserService;
import com.example.flashcards_backend.service.DeckService;
import com.example.flashcards_backend.service.SubjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CsvExportControllerTest extends AbstractIntegrationTest {

  private static final String PATH = "/export";

  @Autowired private SubjectService subjectService;
  @Autowired private CurrentUserService currentUserService;
  @Autowired private CsvExportService csvExportService;
  @Autowired private DeckService deckService;
  @Autowired private DeckRepository deckRepository;
  @Autowired private CardRepository cardRepository;

  Deck deck1;
  Deck deck2;
  Card card1;
  Card card2;

  @BeforeEach
  void setUp() {
    deck1 = Deck.builder().name("Deck 1").subject(subject1).user(testUser).build();
    deck2 = Deck.builder().name("Deck 2").subject(subject1).user(testUser).build();
    deckRepository.saveAllAndFlush(List.of(deck1, deck2));
    card1 = Card.builder().front("Front 1").back("Back 1").user(testUser).subject(subject1).build();
    card2 = Card.builder().front("Front 2").back("Back 2").user(testUser).subject(subject1).build();
    cardRepository.saveAllAndFlush(List.of(card1, card2));
    card1.addDecks(Set.of(deck1, deck2));
    card2.addDecks(Set.of(deck1));
    cardRepository.saveAllAndFlush(List.of(card1, card2));
  }

  @Test
  void exportSubjectCsv_existingSubject_exportsAllCards() throws Exception {
    MvcResult mvcResult =
        mockMvc
            .perform(get(PATH + "/subject/" + subject1.getId()).with(jwt))
            .andExpect(status().isOk())
            .andExpect(
                header()
                    .string(
                        "Content-Disposition", "attachment; filename=\"subject_Subject 1.csv\""))
            .andExpect(header().string("Content-Type", "text/csv"))
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString().replace("\r\n", "\n").trim();
    String expectedContents =
        """
        front,back,hint_front,hint_back,decks
        Front 1,Back 1,,,Deck 1;Deck 2
        Front 2,Back 2,,,Deck 1
        """
            .trim();
    assertThat(content).isEqualTo(expectedContents);
  }

  @Test
  void exportDeckCsv_existingDeck_exportsAllCardsInDeck_andIncludesOnlySpecifiedDeck()
      throws Exception {
    MvcResult mvcResult =
        mockMvc
            .perform(get(PATH + "/deck/" + deck1.getId()).with(jwt))
            .andExpect(status().isOk())
            .andExpect(
                header().string("Content-Disposition", "attachment; filename=\"deck_Deck 1.csv\""))
            .andExpect(header().string("Content-Type", "text/csv"))
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString().replace("\r\n", "\n").trim();
    String expectedContents =
        """
        front,back,hint_front,hint_back,decks
        Front 1,Back 1,,,Deck 1
        Front 2,Back 2,,,Deck 1
        """
            .trim();
    assertThat(content).isEqualTo(expectedContents);
  }
}
