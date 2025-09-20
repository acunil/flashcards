package com.example.flashcards_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.flashcards_backend.exception.DeckNotFoundException;
import com.example.flashcards_backend.exception.SubjectNotFoundException;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.CardExportRowProjection;
import com.example.flashcards_backend.repository.CardExportRowProjectionImpl;
import com.example.flashcards_backend.repository.CardRepository;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CsvExportServiceTest {

  @Mock private CardRepository cardRepository;

  @Mock private SubjectService subjectService;

  @Mock private DeckService deckService;

  private CsvExportService csvExportService;

  Subject subject;
  Deck deck;
  CardExportRowProjection cardRow1;
  CardExportRowProjection cardRow2;
  CardExportRowProjection cardRow3;

  @BeforeEach
  void setUp() {
    csvExportService = new CsvExportServiceImpl(cardRepository, subjectService, deckService);
    subject = Subject.builder().id(1L).name("Subject 1").build();
    deck = Deck.builder().id(1L).name("Deck 1").subject(subject).build();
    cardRow1 = new CardExportRowProjectionImpl(1L, "Front 1", "Back 1", "Hint Front 1", "Hint Back 1", "Deck 1");
    cardRow2 = new CardExportRowProjectionImpl(1L, "Front 1", "Back 1", "Hint Front 1", "Hint Back 1", "Deck 2");
    cardRow3 = new CardExportRowProjectionImpl(2L, "Front 2", "Back 2", null, null, "Deck 1");
  }

  @Test
  void exportCards_fromDeck_createsCsvWithCards_andDecksOnlyListsSearchedDeck() throws IOException {
    when(deckService.getDeckById(1L)).thenReturn(deck);
    when(cardRepository.findExportRowsByDeckId(1L)).thenReturn(List.of(cardRow1, cardRow3));

    byte[] bytes = csvExportService.exportCards(CsvExportService.CardSource.DECK, 1L);
    assertThat(bytes).isNotEmpty();
    String csvContent = new String(bytes).replace("\r\n", "\n").trim();
    String expectedContents =
        """
        front,back,hint_front,hint_back,decks
        Front 1,Back 1,Hint Front 1,Hint Back 1,Deck 1
        Front 2,Back 2,,,Deck 1
        """
            .trim(); // Note that card2 only lists "Deck 1" because we are exporting from that deck

    assertThat(csvContent).isEqualTo(expectedContents).doesNotContain("Deck 2");
  }

  @Test
  void exportCards_fromSubject_createsCsvWithCards() throws IOException {
    when(subjectService.findById(1L)).thenReturn(subject);
    when(cardRepository.findExportRowsBySubjectId(1L)).thenReturn(List.of(cardRow1, cardRow2, cardRow3));

    byte[] bytes = csvExportService.exportCards(CsvExportService.CardSource.SUBJECT, 1L);
    assertThat(bytes).isNotEmpty();
    String csvContent = new String(bytes).replace("\r\n", "\n").trim();
    String expectedContents =
        """
        front,back,hint_front,hint_back,decks
        Front 1,Back 1,Hint Front 1,Hint Back 1,Deck 1;Deck 2
        Front 2,Back 2,,,Deck 1
        """
            .trim();

    assertThat(csvContent).isEqualTo(expectedContents);
  }

  @Test
  void exportCards_fromSubject_notFound_throwsException() {
    when(subjectService.findById(1L)).thenThrow(new SubjectNotFoundException(1L));
    assertThatThrownBy(() -> csvExportService.exportCards(CsvExportService.CardSource.SUBJECT, 1L))
        .isInstanceOf(SubjectNotFoundException.class)
        .extracting("message")
        .isEqualTo("Subject not found with id: 1");
  }

  @Test
  void exportCards_fromDeck_notFound_throwsException() {
    when(deckService.getDeckById(1L)).thenThrow(new DeckNotFoundException(1L));
    assertThatThrownBy(() -> csvExportService.exportCards(CsvExportService.CardSource.DECK, 1L))
        .isInstanceOf(DeckNotFoundException.class)
        .extracting("message")
        .isEqualTo("Deck not found with id: 1");
  }

}
