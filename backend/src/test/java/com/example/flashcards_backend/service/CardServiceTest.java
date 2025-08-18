package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CardRequest;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.dto.CardCreationResult;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.CardDeckRowProjection;
import com.example.flashcards_backend.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.example.flashcards_backend.testutils.ShuffleTestUtils.assertEventuallyReorders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

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
    private Card card3;
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
        card3 = Card.builder()
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

        // set up cardDeckRowProjections
        when(cardDeckRowProjection1.getDeckId()).thenReturn(1L);
        when(cardDeckRowProjection1.getDeckName()).thenReturn("Deck 1");
        when(cardDeckRowProjection1.getCardId()).thenReturn(CARD_1_ID);
        when(cardDeckRowProjection1.getFront()).thenReturn("Front 1");
        when(cardDeckRowProjection1.getBack()).thenReturn("Back 1");
        when(cardDeckRowProjection1.getAvgRating()).thenReturn(3.0);

        when(cardDeckRowProjection2.getDeckId()).thenReturn(2L);
        when(cardDeckRowProjection2.getDeckName()).thenReturn("Deck 2");
        when(cardDeckRowProjection2.getCardId()).thenReturn(CARD_2_ID);

        when(cardDeckRowProjection3.getDeckId()).thenReturn(1L);
        when(cardDeckRowProjection3.getDeckName()).thenReturn("Deck 1");
        when(cardDeckRowProjection3.getCardId()).thenReturn(CARD_3_ID);

        when(cardRepository.findAllCardDeckRows())
                .thenReturn(List.of(cardDeckRowProjection1, cardDeckRowProjection2, cardDeckRowProjection3));

    }

    @Test
    void testGetCardById() {
        Card foundCard = cardService.getCardById(CARD_1_ID);
        assertThat(foundCard).isEqualTo(card1);

        Card otherCard = cardService.getCardById(CARD_2_ID);
        assertThat(otherCard).isEqualTo(card2);
    }

    @Test
    void testGetCardById_throwsExceptionIfCardNotFound() {
        assertThatThrownBy(() -> cardService.getCardById(99L))
            .isInstanceOf(CardNotFoundException.class)
            .extracting("message")
            .isEqualTo("Card not found with id: 99");
    }

    @Test
    void testGetAllCards_noSubjectSpecified() {
        var cards = cardService.getAllCardResponsesFromSubject(null);
        assertThat(cards).hasSize(3);
        verify(cardRepository).findAllCardDeckRows();
    }

    @Test
    void testGetAllCards_withSubjectSpecified() {
        when(cardRepository.findAllCardDeckRowsBySubjectId(SUBJECT_ID)).thenReturn(List.of(cardDeckRowProjection1, cardDeckRowProjection2));
        var cards = cardService.getAllCardResponsesFromSubject(SUBJECT_ID);
        assertThat(cards).hasSize(2);
        verify(cardRepository).findAllCardDeckRowsBySubjectId(SUBJECT_ID);
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
        CardCreationResult result = cardService.createCard(request);

        // then
        assertThat(result.alreadyExisted()).isTrue();
        assertThat(result.card()).isEqualTo(existing);

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
        CardCreationResult result = cardService.createCard(request);

        // then
        assertThat(result.alreadyExisted()).isFalse();
        assertThat(result.card().getId()).isEqualTo(20L);
        assertThat(result.card().getFront()).isEqualTo(front);
        assertThat(result.card().getBack()).isEqualTo(back);
        assertThat(result.card().getSubject()).isEqualTo(subject);
        assertThat(result.card().getDecks()).containsExactlyElementsOf(decks);

        verify(cardRepository).findBySubjectIdAndFrontAndBack(SUBJECT_ID, front, back);
        verify(subjectService).findById(SUBJECT_ID);
        verify(cardDeckService).getOrCreateDecksByNames(Set.of(deck1.getName(), deck2.getName()));
        verify(cardRepository).saveAndFlush(any(Card.class));
    }

    @Test
    void testUpdateCard_removesDecks_andDoesNotCallCardDeckService() {
        when(cardDeckService.getOrCreateDecksByNames(any())).thenReturn(Set.of());

        // request contains empty deckNamesDto
        CardRequest request = CardRequest.of("Updated Front", "Updated Back", SUBJECT_ID);
        cardService.updateCard(CARD_1_ID, request);

        Card updatedCard = cardService.getCardById(CARD_1_ID);
        assertThat(updatedCard.getFront()).isEqualTo("Updated Front");
        assertThat(updatedCard.getBack()).isEqualTo("Updated Back");

        // Service was not called with null deckNamesDto
        verifyNoMoreInteractions(cardDeckService);
    }

    @Test
    void testUpdateCard_updatesDecks() {
        Set<Deck> decks = Set.of(deck2);
        when(cardDeckService.getOrCreateDecksByNames(any())).thenReturn(decks);

        CardRequest updateRequest = CardRequest.of("Updated Front", "Updated Back", SUBJECT_ID, deck2.getName());
        cardService.updateCard(CARD_3_ID, updateRequest);

        Card updatedCard = cardService.getCardById(CARD_3_ID);
        assertThat(updatedCard.getFront()).isEqualTo("Updated Front");
        assertThat(updatedCard.getBack()).isEqualTo("Updated Back");
        assertThat(updatedCard.getDecks()).containsExactly(deck2);

        verify(cardDeckService).getOrCreateDecksByNames(Set.of(deck2.getName()));
    }

    @Test
    void testUpdateCard_doesNotUpdateDecks_whenSameDecks() {
        // card already has the same decks, so no need to update
        CardRequest request = CardRequest.of("Front 1", "Back 1", SUBJECT_ID, deck1.getName(), deck2.getName());
        cardService.updateCard(CARD_1_ID, request);

        Card updatedCard = cardService.getCardById(CARD_1_ID);
        assertThat(updatedCard.getFront()).isEqualTo("Front 1");
        assertThat(updatedCard.getBack()).isEqualTo("Back 1");
        assertThat(updatedCard.getDecks()).containsExactlyInAnyOrder(deck1, deck2);

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

}