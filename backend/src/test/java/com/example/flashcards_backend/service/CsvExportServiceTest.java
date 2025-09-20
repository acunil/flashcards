package com.example.flashcards_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.flashcards_backend.exception.SubjectNotFoundException;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.CardExportProjection;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.DeckRepository;
import com.example.flashcards_backend.repository.SubjectRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CsvExportServiceTest {

  @Mock private CardRepository cardRepository;

  @Mock private SubjectRepository subjectRepository;

  @Mock private DeckRepository deckRepository;

  private CsvExportService csvExportService;

  Subject subject;
  Deck deck;
  CardExportProjection card1;
  CardExportProjection card2;

  @BeforeEach
  void setUp() {
    csvExportService = new CsvExportServiceImpl(cardRepository, subjectRepository, deckRepository);
    subject = Subject.builder().id(1L).name("Subject 1").build();
    deck = Deck.builder().id(1L).name("Deck 1").subject(subject).build();
    card1 =
        cardExportProjection("Front 1", "Back 1", "Hint Front 1", "Hint Back 1", List.of("Deck 1"));
    card2 = cardExportProjection("Front 2", "Back 2", null, null, List.of("Deck 1", "Deck 2"));
  }

  @Test
  void exportCards_fromDeck_createsCsvWithCards_andDecksOnlyListsSearchedDeck() throws IOException {
    when(deckRepository.findById(1L)).thenReturn(Optional.of(deck));
    when(cardRepository.findExportDataByDeckId(1L)).thenReturn(List.of(card1, card2));

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
    when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
    when(cardRepository.findExportDataBySubjectId(1L)).thenReturn(List.of(card1, card2));

    byte[] bytes = csvExportService.exportCards(CsvExportService.CardSource.SUBJECT, 1L);
    assertThat(bytes).isNotEmpty();
    String csvContent = new String(bytes).replace("\r\n", "\n").trim();
    String expectedContents =
        """
        front,back,hint_front,hint_back,decks
        Front 1,Back 1,Hint Front 1,Hint Back 1,Deck 1
        Front 2,Back 2,,,Deck 1;Deck 2
        """
            .trim();

    assertThat(csvContent).isEqualTo(expectedContents);
  }

//  @Test
//  void exportCards_fromSubject_notFound_throwsException() throws IOException {
//    when(subjectRepository.findById(1L)).thenReturn(Optional.empty());
//    assertThatThrownBy(csvExportService.exportCards(CsvExportService.CardSource.SUBJECT, 1L))
//        .isInstanceOf(SubjectNotFoundException.class)
//        .hasMessageContaining("Subject with id 1 not found");
//  }

  CardExportProjection cardExportProjection(
      String front, String back, String hintFront, String hintBack, List<String> decks) {
    return new CardExportProjection() {
      @Override
      public String getFront() {
        return front;
      }

      @Override
      public String getBack() {
        return back;
      }

      @Override
      public String getHintBack() {
        return hintBack;
      }

      @Override
      public String getHintFront() {
        return hintFront;
      }

      @Override
      public List<String> getDecks() {
        return decks;
      }
    };
  }
}
