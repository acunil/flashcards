package com.example.flashcards_backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;

class DeckTest {
//
//    Card card1;
//    Card card2;
//    Card card3;
//    Deck deck;
//
//    @BeforeEach
//    void setUp() {
//        card1 = new Card();
//        card2 = new Card();
//        card3 = new Card();
//        deck = new Deck();
//
//        card1.setId(1L);
//        card2.setId(2L);
//        card3.setId(3L);
//        deck.setId(1L);
//    }
//
//    @Test
//    void testAddCard() {
//        card1.setId(1L);
//
//        assertThat(deck.getCards()).isEmpty();
//        assertThat(card1.getDecks()).isEmpty();
//
//        deck.addCard(card1);
//
//        assertThat(deck.getCards()).containsExactly(card1);
//        assertThat(card1.getDecks()).containsExactly(deck);
//    }
//
//    @Test
//    void testAddCard_null() {
//        assertThat(deck.getCards()).isEmpty();
//
//        deck.addCard(null);
//
//        assertThat(deck.getCards()).isEmpty();
//    }
//
//    @Test
//    void testAddCards() {
//        assertThat(deck.getCards()).isEmpty();
//
//        deck.addCards(Set.of(card1, card2, card3));
//
//        assertThat(deck.getCards()).containsExactlyInAnyOrder(card1, card2, card3);
//        assertThat(card1.getDecks()).containsExactly(deck);
//        assertThat(card2.getDecks()).containsExactly(deck);
//        assertThat(card3.getDecks()).containsExactly(deck);
//    }
//
//    @Test
//    void testAddCards_null() {
//        assertThat(deck.getCards()).isEmpty();
//
//        deck.addCards(null);
//
//        assertThat(deck.getCards()).isEmpty();
//    }
//
//    @Test
//    void testRemoveCard() {
//        deck.addCard(card1);
//        deck.addCard(card2);
//
//        assertThat(card1.getDecks()).contains(deck);
//        assertThat(card2.getDecks()).contains(deck);
//
//        deck.removeCard(card1);
//
//        assertThat(deck.getCards()).doesNotContain(card1);
//        assertThat(deck.getCards()).contains(card2);
//
//        assertThat(card1.getDecks()).doesNotContain(deck);
//        assertThat(card2.getDecks()).contains(deck);
//    }
//
//    @Test
//    void testRemoveCard_null() {
//        deck.addCard(card1);
//        deck.addCard(card2);
//
//        assertThat(deck.getCards()).containsExactlyInAnyOrder(card1, card2);
//
//        deck.removeCard(null);
//
//        assertThat(deck.getCards()).containsExactlyInAnyOrder(card1, card2);
//    }
//
//    @Test
//    void testRemoveCard_notInDeck() {
//        deck.addCard(card1);
//        deck.addCard(card2);
//
//        assertThat(deck.getCards()).containsExactlyInAnyOrder(card1, card2);
//
//        Card cardNotInDeck = new Card();
//        cardNotInDeck.setId(3L);
//
//        deck.removeCard(cardNotInDeck);
//
//        assertThat(deck.getCards()).containsExactlyInAnyOrder(card1, card2);
//    }
//
//    @Test
//    void testRemoveCards() {
//        deck.addCard(card1);
//        deck.addCard(card2);
//        deck.addCard(card3);
//
//        assertThat(deck.getCards()).containsExactlyInAnyOrder(card1, card2, card3);
//
//        deck.removeCards(Set.of(card1, card2));
//
//        assertThat(deck.getCards()).containsExactly(card3);
//        assertThat(card1.getDecks()).doesNotContain(deck);
//        assertThat(card2.getDecks()).doesNotContain(deck);
//    }
//
//    @Test
//    void testRemoveCards_null() {
//        deck.addCard(card1);
//        deck.addCard(card2);
//        deck.addCard(card3);
//
//        assertThat(deck.getCards()).containsExactlyInAnyOrder(card1, card2, card3);
//
//        deck.removeCards(null);
//
//        assertThat(deck.getCards()).containsExactlyInAnyOrder(card1, card2, card3);
//    }
//
//    @Test
//    void testRemoveAllCards() {
//        deck.addCard(card1);
//        deck.addCard(card2);
//        deck.addCard(card3);
//        assertThat(deck.getCards()).containsExactlyInAnyOrder(card1, card2, card3);
//        deck.removeAllCards();
//        assertThat(deck.getCards()).isEmpty();
//        assertThat(card1.getDecks()).doesNotContain(deck);
//        assertThat(card2.getDecks()).doesNotContain(deck);
//        assertThat(card3.getDecks()).doesNotContain(deck);
//    }
//
//    @Test
//    void testHasCard() {
//        deck.addCard(card1);
//        deck.addCard(card2);
//
//        assertThat(deck.hasCard(card1)).isTrue();
//        assertThat(deck.hasCard(card2)).isTrue();
//        assertThat(deck.hasCard(card3)).isFalse();
//    }
//
//    @Test
//    void testHasNotCard_byCard() {
//        deck.addCard(card1);
//        deck.addCard(card2);
//
//        assertThat(deck.hasNotCard(card1)).isFalse();
//        assertThat(deck.hasNotCard(card2)).isFalse();
//        assertThat(deck.hasNotCard(card3)).isTrue();
//    }
//
//    @Test
//    void testHasNotCard_byLong() {
//        deck.addCard(card1);
//        deck.addCard(card2);
//
//        assertThat(deck.hasNotCard(1L)).isFalse();
//        assertThat(deck.hasNotCard(2L)).isFalse();
//        assertThat(deck.hasNotCard(3L)).isTrue();
//    }
//
//    @Test
//    void testGetCardById() {
//        deck.addCard(card1);
//        deck.addCard(card2);
//
//        assertThat(deck.getCardById(1L)).isEqualTo(card1);
//        assertThat(deck.getCardById(2L)).isEqualTo(card2);
//
//        assertThatThrownBy(() -> deck.getCardById(3L))
//            .isInstanceOf(IllegalArgumentException.class)
//            .hasMessageContaining("Card with ID 3 not found in this deck");
//    }
//
//    @Test
//    void testGetCardIds() {
//        deck.addCard(card1);
//        deck.addCard(card2);
//        deck.addCard(card3);
//
//        Set<Long> cardIds = deck.getCardIds();
//
//        assertThat(cardIds).containsExactlyInAnyOrder(1L, 2L, 3L);
//    }

}