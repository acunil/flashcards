package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CardRequest;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.CardCreationResult;
import com.example.flashcards_backend.model.Deck;
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
    public static final double THRESHOLD = 3.0;

    private CardService cardService;
    private Card card1;
    private Card card2;
    private Card card3;
    private List<Card> originalCards;
    private Deck deck1;
    private Deck deck2;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardHistoryService cardHistoryService;

    @Mock
    private CardDeckService cardDeckService;

    @BeforeEach
    void setUp() {
        cardService = new CardService(cardRepository, cardHistoryService, cardDeckService);
        deck1 = Deck.builder()
            .id(1L)
            .name("Deck 1")
            .build();
        deck2 = Deck.builder()
            .id(2L)
            .name("Deck 2")
            .build();
        card1 = Card.builder()
            .id(CARD_1_ID)
            .front("Front 1")
            .back("Back 1")
            .build();
        card2 = Card.builder()
            .id(CARD_2_ID)
            .front("Front 2")
            .back("Back 2")
            .build();
        card3 = Card.builder()
            .id(CARD_3_ID)
            .front("Front 3")
            .back("Back 3")
            .build();
        card1.addDecks(Set.of(deck1, deck2));
        card2.addDecks(Set.of(deck1));
        originalCards = List.of(card1, card2, card3);

        when(cardRepository.findById(CARD_1_ID)).thenReturn(Optional.of(card1));
        when(cardRepository.findById(CARD_2_ID)).thenReturn(Optional.of(card2));
        when(cardRepository.findById(CARD_3_ID)).thenReturn(Optional.of(card3));
        when(cardRepository.findAll()).thenReturn(originalCards);
    }

    @Test
    void testGetCardById() {
        Card foundCard = cardService.getCardById(CARD_1_ID);
        assertThat(foundCard).isEqualTo(card1);

        Card otherCard = cardService.getCardById(CARD_2_ID);
        assertThat(otherCard).isEqualTo(card2);
    }

    @Test
    void testCreateCard() {
        String newFront = "New Front";
        String newBack = "New Back";
        long newId = 4L;
        when(cardRepository.createIfUnique(newFront, newBack))
            .thenReturn(Map.of(
                "id",    newId,
                "front", newFront,
                "back",  newBack,
                "alreadyExisted", false
            ));

        CardRequest request = CardRequest.of(newFront, newBack);

        CardCreationResult cardCreationResult = cardService.createCard(request);
        assertThat(cardCreationResult.card().getId()).isNotNull().isEqualTo(newId);
        assertThat(cardCreationResult.card().getFront()).isEqualTo(newFront);
        assertThat(cardCreationResult.card().getBack()).isEqualTo(newBack);
        assertThat(cardCreationResult.alreadyExisted()).isFalse();

        verify(cardRepository).createIfUnique(newFront, newBack);
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void testUpdateCard_removesDecks_andDoesNotCallCardDeckService() {
        when(cardDeckService.getOrCreateDecksByNames(any())).thenReturn(Set.of());

        // request contains empty deckNamesDto
        CardRequest request = CardRequest.of("Updated Front", "Updated Back");
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

        CardRequest updateRequest = CardRequest.of("Updated Front", "Updated Back", deck2.getName());
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
        CardRequest request = CardRequest.of("Front 1", "Back 1", deck1.getName(), deck2.getName());
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