package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CardRequest;
import com.example.flashcards_backend.dto.CardResponse;
import com.example.flashcards_backend.dto.CreateCardResponse;
import com.example.flashcards_backend.dto.HintRequest;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.CardDeckRowProjection;
import com.example.flashcards_backend.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.example.flashcards_backend.testutils.ShuffleTestUtils.assertEventuallyReorders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(SpringExtension.class)
class CardServiceTest {
    public static final long CARD_1_ID = 1L;
    public static final long CARD_2_ID = 2L;
    public static final long CARD_3_ID = 3L;
    public static final long SUBJECT_ID = 1L;
    public static final double THRESHOLD = 3.0;

    private CardService cardService;
    private Card card1;
    private Card card2;
    private List<Card> originalCards;
    private Deck deck1;
    private Deck deck2;
    private Subject subject;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardHistoryService cardHistoryService;

    @Mock
    private CardDeckService cardDeckService;

    @Mock
    private SubjectService subjectService;

    @Mock
    private CardDeckRowProjection cardDeckRowProjection1;

    @Mock
    private CardDeckRowProjection cardDeckRowProjection2;

    @Mock
    private CardDeckRowProjection cardDeckRowProjection3;

    @BeforeEach
    void setUp() {
        cardService = new CardService(cardRepository, cardHistoryService, cardDeckService, subjectService);
        subject = Subject.builder().name("Subject 1").id(1L).build();
        deck1 = Deck.builder()
            .id(1L)
            .name("Deck 1")
            .subject(subject)
            .build();
        deck2 = Deck.builder()
            .id(2L)
            .name("Deck 2")
            .subject(subject)
            .build();
        card1 = Card.builder()
            .id(CARD_1_ID)
            .front("Front 1")
            .back("Back 1")
            .subject(subject)
            .build();
        card2 = Card.builder()
            .id(CARD_2_ID)
            .front("Front 2")
            .back("Back 2")
            .subject(subject)
            .build();
        Card card3 = Card.builder()
                .id(CARD_3_ID)
                .front("Front 3")
                .back("Back 3")
                .subject(subject)
                .build();
        card1.addDecks(Set.of(deck1, deck2));
        card2.addDecks(Set.of(deck1));
        originalCards = List.of(card1, card2, card3);

        when(cardRepository.findById(CARD_1_ID)).thenReturn(Optional.of(card1));
        when(cardRepository.findById(CARD_2_ID)).thenReturn(Optional.of(card2));
        when(cardRepository.findById(CARD_3_ID)).thenReturn(Optional.of(card3));

        // Card 1 has 2 decks, 1 with name Deck 1 and 1 with name Deck 2
        when(cardDeckRowProjection1.getCardId()).thenReturn(CARD_1_ID);
        when(cardDeckRowProjection1.getDeckId()).thenReturn(1L);
        when(cardDeckRowProjection1.getDeckName()).thenReturn("Deck 1");
        when(cardDeckRowProjection1.getFront()).thenReturn("Front 1");
        when(cardDeckRowProjection1.getBack()).thenReturn("Back 1");
        when(cardDeckRowProjection1.getAvgRating()).thenReturn(3.0);
        when(cardDeckRowProjection1.getViewCount()).thenReturn(2);
        when(cardDeckRowProjection1.getLastViewed()).thenReturn(LocalDateTime.now());

        when(cardDeckRowProjection2.getCardId()).thenReturn(CARD_1_ID);
        when(cardDeckRowProjection2.getDeckId()).thenReturn(2L);
        when(cardDeckRowProjection2.getDeckName()).thenReturn("Deck 2");
        when(cardDeckRowProjection2.getFront()).thenReturn("Front 1");
        when(cardDeckRowProjection2.getBack()).thenReturn("Back 1");

        // Card 2 has 1 deck, 1 with name Deck 1
        when(cardDeckRowProjection3.getCardId()).thenReturn(CARD_2_ID);
        when(cardDeckRowProjection3.getDeckId()).thenReturn(1L);
        when(cardDeckRowProjection3.getDeckName()).thenReturn("Deck 1");

        when(cardRepository.findCardDeckRowsByCardId(CARD_1_ID)).thenReturn(List.of(cardDeckRowProjection1, cardDeckRowProjection2));

        when(cardRepository.findCardDeckRows(null, null))
                .thenReturn(List.of(cardDeckRowProjection1, cardDeckRowProjection2, cardDeckRowProjection3));

    }

    @Test
    void testGetCardById() {
        when(cardRepository.findCardDeckRowsByCardId(CARD_2_ID)).thenReturn(List.of(cardDeckRowProjection3));


        CardResponse foundCard = cardService.getCardResponseById(CARD_1_ID);
        assertThat(foundCard.id()).isEqualTo(CARD_1_ID);
        assertThat(foundCard.front()).isEqualTo("Front 1");
        assertThat(foundCard.back()).isEqualTo("Back 1");
        assertThat(foundCard.decks()).hasSize(2);
        assertThat(foundCard.avgRating()).isEqualTo(3.0);
        assertThat(foundCard.viewCount()).isEqualTo(2);
        assertThat(foundCard.lastViewed()).isNotNull();

        var otherCard = cardService.getCardResponseById(CARD_2_ID);
        assertThat(otherCard.id()).isEqualTo(CARD_2_ID);
    }

    @Test
    void testGetCardById_throwsExceptionIfCardNotFound() {
        assertThatThrownBy(() -> cardService.getCardResponseById(99L))
            .isInstanceOf(CardNotFoundException.class)
            .extracting("message")
            .isEqualTo("Card not found with id: 99");
    }

    @Test
    void testGetAllCards_noSubjectSpecified() {
        when(cardRepository.findCardDeckRowsBySubjectId(null))
                .thenReturn(List.of(cardDeckRowProjection1, cardDeckRowProjection2, cardDeckRowProjection3));
        var cards = cardService.getAllCardResponsesFromSubject(null);
        assertThat(cards).hasSize(2);
        verify(cardRepository).findCardDeckRowsBySubjectId(null);
    }

    @Test
    void testGetAllCards_withSubjectSpecified() {
        when(cardRepository.findCardDeckRowsBySubjectId(SUBJECT_ID)).thenReturn(List.of(cardDeckRowProjection1, cardDeckRowProjection2, cardDeckRowProjection3));
        var cards = cardService.getAllCardResponsesFromSubject(SUBJECT_ID);
        assertThat(cards).hasSize(2);
        verify(cardRepository).findCardDeckRowsBySubjectId(SUBJECT_ID);
    }

    @Test
    void createCard_whenCardAlreadyExists_returnsExistingCard() {
        // given
        String front = "Existing Front";
        String back = "Existing Back";
        Card existing = Card.builder()
                .id(10L)
                .front(front)
                .back(back)
                .subject(subject)
                .build();

        when(cardRepository.findBySubjectIdAndFrontAndBack(SUBJECT_ID, front, back))
                .thenReturn(Optional.of(existing));

        CardRequest request = CardRequest.of(front, back, SUBJECT_ID);

        // when
        var  result = cardService.createCard(request);

        // then
        assertThat(result.alreadyExisted()).isTrue();
        assertThat(result.front()).isEqualTo(existing.getFront());
        assertThat(result.back()).isEqualTo(existing.getBack());

        verify(cardRepository).findBySubjectIdAndFrontAndBack(SUBJECT_ID, front, back);
        verifyNoMoreInteractions(cardRepository, subjectService, cardDeckService);
    }

    @Test
    void createCard_whenNewCard_savesAndReturnsCard() {
        // given
        String front = "New Front";
        String back = "New Back";

        when(cardRepository.findBySubjectIdAndFrontAndBack(SUBJECT_ID, front, back))
                .thenReturn(Optional.empty());

        when(subjectService.findById(SUBJECT_ID)).thenReturn(subject);

        Set<Deck> decks = Set.of(deck1, deck2);
        when(cardDeckService.getOrCreateDecksByNames(anySet())).thenReturn(decks);

        Card saved = Card.builder()
                .id(20L)
                .front(front)
                .back(back)
                .subject(subject)
                .decks(decks)
                .build();

        when(cardRepository.saveAndFlush(any(Card.class))).thenReturn(saved);

        CardRequest request = CardRequest.of(front, back, SUBJECT_ID, deck1.getName(), deck2.getName());

        // when
        CreateCardResponse result = cardService.createCard(request);

        // then
        assertThat(result.alreadyExisted()).isFalse();
        assertThat(result.id()).isEqualTo(20L);
        assertThat(result.front()).isEqualTo(front);
        assertThat(result.back()).isEqualTo(back);
        assertThat(result.decks()).hasSize(decks.size());

        verify(cardRepository).findBySubjectIdAndFrontAndBack(SUBJECT_ID, front, back);
        verify(subjectService).findById(SUBJECT_ID);
        verify(cardDeckService).getOrCreateDecksByNames(Set.of(deck1.getName(), deck2.getName()));
        verify(cardRepository).saveAndFlush(any(Card.class));
    }

    @Test
    void testUpdateCard_removesDecks_andDoesNotCallCardDeckService() {
        when(cardRepository.findById(CARD_1_ID)).thenReturn(Optional.of(card1));
        when(cardDeckService.getOrCreateDecksByNames(any())).thenReturn(Set.of());

        // request contains empty deckNamesDto, so expect decks to be cleared
        CardRequest request = CardRequest.builder()
                .front("Updated Front")
                .back("Updated Back")
                .subjectId(SUBJECT_ID)
                .hintFront("Updated Hint Front")
                .hintBack("Updated Hint Back")
                .deckNames(new HashSet<>())
                .build();
        cardService.updateCard(CARD_1_ID, request);

        verify(cardRepository).findById(CARD_1_ID);

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).saveAndFlush(captor.capture());
        when(cardRepository.saveAndFlush(captor.capture())).thenReturn(card1);

        // check that card has been updated, and decks have been cleared
        Card updated = captor.getValue();
        assertThat(updated.getFront()).isEqualTo("Updated Front");
        assertThat(updated.getBack()).isEqualTo("Updated Back");
        assertThat(updated.getHintFront()).isEqualTo("Updated Hint Front");
        assertThat(updated.getHintBack()).isEqualTo("Updated Hint Back");
        assertThat(updated.getDecks()).isEmpty();
        assertThat(updated.getSubject()).isEqualTo(subject);

        // Service was not called with null deckNamesDto
        verifyNoMoreInteractions(cardDeckService);
    }

    @Test
    void testUpdateCard_updatesDecks() {
        when(cardRepository.findById(CARD_1_ID)).thenReturn(Optional.of(card1));
        when(cardDeckService.getOrCreateDecksByNames(any())).thenReturn(Set.of(deck2));

        // request contains different decks, so expect decks to be overwritten
        CardRequest request = CardRequest.builder()
                .front("Updated Front")
                .back("Updated Back")
                .subjectId(SUBJECT_ID)
                .hintFront("Updated Hint Front")
                .hintBack("Updated Hint Back")
                .deckNames(Set.of("Deck 2"))
                .build();
        cardService.updateCard(CARD_1_ID, request);

        verify(cardRepository).findById(CARD_1_ID);

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).saveAndFlush(captor.capture());
        when(cardRepository.saveAndFlush(captor.capture())).thenReturn(card1);

        // check that card has been updated, including decks
        Card updated = captor.getValue();
        assertThat(updated.getFront()).isEqualTo("Updated Front");
        assertThat(updated.getBack()).isEqualTo("Updated Back");
        assertThat(updated.getHintFront()).isEqualTo("Updated Hint Front");
        assertThat(updated.getHintBack()).isEqualTo("Updated Hint Back");
        assertThat(updated.getDecks())
                .hasSize(1)
                .extracting("name")
                .containsExactly("Deck 2");
        assertThat(updated.getSubject()).isEqualTo(subject);

        // Service was called with requested deck
        verify(cardDeckService).getOrCreateDecksByNames(Set.of("Deck 2"));
    }

    @Test
    void testUpdateCard_doesNotUpdateDecks_whenSameDecks() {
        // card already has the same decks, so no need to update
        CardRequest request = CardRequest.of("Front 1", "Back 1", SUBJECT_ID, deck1.getName(), deck2.getName());
        cardService.updateCard(CARD_1_ID, request);

        CardResponse updatedCard = cardService.getCardResponseById(CARD_1_ID);
        assertThat(updatedCard.front()).isEqualTo("Front 1");
        assertThat(updatedCard.back()).isEqualTo("Back 1");
        assertThat(updatedCard.decks())
                .hasSize(2)
                .extracting("name")
                .containsExactlyInAnyOrder(deck1.getName(), deck2.getName());

        verifyNoMoreInteractions(cardDeckService);
    }

    @Test
    void rate_Card_existingCard_callsCardHistoryService() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(new Card()));
        cardService.rateCard(10L, 3);
        verify(cardHistoryService).recordRating(10L, 3);
    }

    @Test
    void rate_Card_missingCard_throwsException() {
        doThrow(new CardNotFoundException(99L)).when(cardHistoryService).recordRating(99L, 4);
        assertThatThrownBy(() -> cardService.rateCard(99L, 4))
            .isInstanceOf(CardNotFoundException.class)
            .extracting("message")
            .isEqualTo("Card not found with id: 99");
    }

    @Test
    void getCardsByMinAvgRating_returnsCardsAboveThreshold() {
        when(cardRepository.findByMinAvgRating(THRESHOLD)).thenReturn(List.of(card1));
        List<Card> result = cardService.getCardsByMinAvgRating(THRESHOLD);
        assertThat(result).containsExactly(card1);
    }

    @Test
    void getCardsByMinAvgRating_shuffledTrue_returnsCardsAboveThresholdEventuallyReordered() {
        when(cardRepository.findByMinAvgRating(THRESHOLD))
            .thenReturn(originalCards);

        assertEventuallyReorders(
            () -> cardService.getCardsByMinAvgRating(THRESHOLD, true),
            originalCards
        );
    }


    @Test
    void getCardsByMinAvgRating_shuffledFalse_alwaysSameOrder() {
        when(cardRepository.findByMinAvgRating(THRESHOLD))
            .thenReturn(originalCards);

        List<Card> result = cardService.getCardsByMinAvgRating(THRESHOLD, false);
        assertThat(result).isSameAs(originalCards);
    }

    @Test
    void getCardsByMaxAvgRating_returnsCardsBelowThreshold() {
        when(cardRepository.findByMaxAvgRating(THRESHOLD)).thenReturn(List.of(card2));
        List<Card> result = cardService.getCardsByMaxAvgRating(THRESHOLD);
        assertThat(result).containsExactly(card2);
    }

    @Test
    void getCardsByMaxAvgRating_shuffledTrue_returnsCardsBelowThresholdEventuallyReordered() {
        when(cardRepository.findByMaxAvgRating(THRESHOLD))
            .thenReturn(originalCards);

        assertEventuallyReorders(
            () -> cardService.getCardsByMaxAvgRating(THRESHOLD, true),
            originalCards
        );
    }

    @Test
    void getCardsByMaxAvgRating_shuffledFalse_alwaysSameOrder() {
        when(cardRepository.findByMaxAvgRating(THRESHOLD))
            .thenReturn(originalCards);

        List<Card> result = cardService.getCardsByMaxAvgRating(THRESHOLD, false);
        assertThat(result).isSameAs(originalCards);
    }

    @Test
    void deleteCard_existingCard_deletesCard() {
        List<Long> ids = List.of(CARD_1_ID);
        cardService.deleteCards(ids);
        verify(cardRepository).deleteCardsById(ids);
    }

    @Test
    void deleteCard_missingCard_throwsException() {
        doThrow(new CardNotFoundException(CARD_3_ID))
                .when(cardRepository).deleteCardsById(List.of(CARD_3_ID));
        assertThatThrownBy(() -> cardService.deleteCards(List.of(CARD_3_ID)))
            .isInstanceOf(CardNotFoundException.class)
            .extracting("message")
            .isEqualTo("Card not found with id: " + CARD_3_ID);
    }

    @Test
    void deleteCard_multipleCards_deletesCards() {
        List<Long> ids = List.of(CARD_1_ID, CARD_2_ID);
        cardService.deleteCards(ids);
        verify(cardRepository).deleteCardsById(ids);
    }

    @Test
    void setHints_whenCardExists_updatesHints() {
        when(cardRepository.findById(CARD_1_ID)).thenReturn(Optional.of(card1));

        var request = HintRequest.builder()
                .hintFront("Front Hint")
                .hintBack("Back Hint")
                .build();

        cardService.setHints(request, CARD_1_ID);

        verify(cardRepository).findById(CARD_1_ID);

        assertThat(card1.getHintFront()).isEqualTo("Front Hint");
        assertThat(card1.getHintBack()).isEqualTo("Back Hint");
    }

}