package com.example.flashcards_backend.service;

import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.DeckRepository;
import com.example.flashcards_backend.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CsvExportServiceTest {

  @Mock private CardRepository cardRepository;

  @Mock private SubjectRepository subjectRepository;

  @Mock private DeckRepository deckRepository;

  private CsvExportService csvExportService;

  Subject subject;
  Deck deck;

  @BeforeEach
  void setUp() {
    csvExportService = new CsvExportServiceImpl(cardRepository, subjectRepository, deckRepository);
    subject = Subject.builder().id(1L).name("Subject 1").build();
    deck = Deck.builder().id(1L).name("Deck 1").subject(subject).build();
  }

  @Test
  void exportCards_fromDeck_createsCsvWithCards() throws IOException {
    when(deckRepository.findById(1L)).thenReturn(Optional.of(deck));

    byte[] bytes = csvExportService.exportCards(CsvExportService.CardSource.DECK, 1L);

  }
}
