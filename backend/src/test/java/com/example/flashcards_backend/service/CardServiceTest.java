package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CardRequest;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardHistoryService cardHistoryService;

    @BeforeEach
    void setUp() {
        cardService = new CardService(cardRepository, cardHistoryService);

        card1 = new Card(CARD_1_ID, "Front 1", "Back 1");
        card2 = new Card(CARD_2_ID, "Front 2", "Back 2");
        card3 = new Card(CARD_3_ID, "Front 3", "Back 3");
        originalCards = List.of(card1, card2, card3);

        when(cardRepository.findById(CARD_1_ID)).thenReturn(Optional.of(card1));
        when(cardRepository.findById(CARD_2_ID)).thenReturn(Optional.of(card2));
        when(cardRepository.findById(CARD_3_ID)).thenReturn(Optional.of(card3));
        when(cardRepository.findAll()).thenReturn(originalCards);
    }

    @Test
    void testGetAllCards() {
         List<Card> cards = cardService.getAll();
         assertThat(cards).containsExactly(card1, card2, card3);
    }

    @Test
    void testGetAllCardsShuffledTrue() {
        assertEventuallyReorders(
            () -> cardService.getAll(true),
            originalCards
        );
    }

    @Test
    void testGetAllCardsShuffledFalse() {
        List<Card> shuffledCards = cardService.getAll(false);
        assertThat(shuffledCards).containsExactly(card1, card2, card3);
    }

    @Test
    void testGetCardById() {
        Card foundCard = cardService.getById(CARD_1_ID);
        assertThat(foundCard).isEqualTo(card1);

        Card otherCard = cardService.getById(CARD_2_ID);
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
                "back",  newBack
            ));

        CardRequest request = new CardRequest(newFront, newBack);

        Card createdCard = cardService.create(request);
        assertThat(createdCard.getId()).isNotNull().isEqualTo(newId);
        assertThat(createdCard.getFront()).isEqualTo(newFront);
        assertThat(createdCard.getBack()).isEqualTo(newBack);

        verify(cardRepository).createIfUnique(newFront, newBack);
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void testUpdateCard() {
        CardRequest request = new CardRequest("Updated Front", "Updated Back");
        cardService.update(CARD_1_ID, request);

        Card updatedCard = cardService.getById(CARD_1_ID);
        assertThat(updatedCard.getFront()).isEqualTo("Updated Front");
        assertThat(updatedCard.getBack()).isEqualTo("Updated Back");
    }

    @Test
    void rate_existingCard_callsCardHistoryService() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(new Card()));
        cardService.rate(10L, 3);
        verify(cardHistoryService).recordRating(10L, 3);
    }

    @Test
    void rate_missingCard_throwsException() {
        when(cardRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cardService.rate(99L, 4))
            .isInstanceOf(CardNotFoundException.class)
            .extracting("message")
            .isEqualTo("Card not found with id: 99");
        verifyNoInteractions(cardHistoryService);
    }

    @Test
    void getByMinAvgRating_returnsCardsAboveThreshold() {
        when(cardRepository.findByMinAvgRating(THRESHOLD)).thenReturn(List.of(card1));
        List<Card> result = cardService.getByMinAvgRating(THRESHOLD);
        assertThat(result).containsExactly(card1);
    }

    @Test
    void getByMinAvgRating_shuffledTrue_returnsCardsAboveThresholdEventuallyReordered() {
        when(cardRepository.findByMinAvgRating(THRESHOLD))
            .thenReturn(originalCards);

        assertEventuallyReorders(
            () -> cardService.getByMinAvgRating(THRESHOLD, true),
            originalCards
        );
    }


    @Test
    void getByMinAvgRating_shuffledFalse_alwaysSameOrder() {
        when(cardRepository.findByMinAvgRating(THRESHOLD))
            .thenReturn(originalCards);

        List<Card> result = cardService.getByMinAvgRating(THRESHOLD, false);
        assertThat(result).isSameAs(originalCards);
    }

    @Test
    void getByMaxAvgRating_returnsCardsBelowThreshold() {
        when(cardRepository.findByMaxAvgRating(THRESHOLD)).thenReturn(List.of(card2));
        List<Card> result = cardService.getByMaxAvgRating(THRESHOLD);
        assertThat(result).containsExactly(card2);
    }

    @Test
    void getByMaxAvgRating_shuffledTrue_returnsCardsBelowThresholdEventuallyReordered() {
        when(cardRepository.findByMaxAvgRating(THRESHOLD))
            .thenReturn(originalCards);

        assertEventuallyReorders(
            () -> cardService.getByMaxAvgRating(THRESHOLD, true),
            originalCards
        );
    }

    @Test
    void getByMaxAvgRating_shuffledFalse_alwaysSameOrder() {
        when(cardRepository.findByMaxAvgRating(THRESHOLD))
            .thenReturn(originalCards);

        List<Card> result = cardService.getByMaxAvgRating(THRESHOLD, false);
        assertThat(result).isSameAs(originalCards);
    }

}