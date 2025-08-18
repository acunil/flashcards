package com.example.flashcards_backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CardTest {

    private Card card;
    private Deck deck1;
    private Deck deck2;
    private Subject subject;

    @BeforeEach
    void setUp() {
        subject = Subject.builder().name("Subject 1").id(1L).build();
        card = Card.builder()
                .front("Front text")
                .back("Back text")
                .subject(subject)
                .build();
        deck1 = Deck.builder().name("Deck 1").id(1L).subject(subject).build();
        deck2 = Deck.builder().name("Deck 2").id(2L).subject(subject).build();
    }

    @Test
    void testAddDecks() {
        card.addDeck(deck1);
        card.addDeck(deck2);

        assertThat(card.getDecks())
            .hasSize(2)
            .contains(deck1, deck2);
    }

    @Test
    void testAddDecks_null() {
        card.addDecks(null);
        assertThat(card.getDecks()).isEmpty();
    }

    @Test
    void testHasDeck() {
        card.addDeck(deck1);
        assertThat(card.hasDeck(deck1)).isTrue();
        assertThat(card.hasDeck(deck2)).isFalse();
    }

    @Test
    void testHasDeck_name() {
        card.addDeck(deck1);
        assertThat(card.hasDeck("Deck 1")).isTrue();
        assertThat(card.hasDeck("Deck 2")).isFalse();
    }

    @Test
    void testHasDeck_id() {
        card.addDeck(deck1);
        assertThat(card.hasDeck(1L)).isTrue();
        assertThat(card.hasDeck(2L)).isFalse();
    }

    @Test
    void testHasNotDeck() {
        card.addDeck(deck1);
        assertThat(card.hasNotDeck(deck1)).isFalse();
        assertThat(card.hasNotDeck(deck2)).isTrue();
    }

    @Test
    void testHasNotDeck_name() {
        card.addDeck(deck1);
        assertThat(card.hasNotDeck("Deck 1")).isFalse();
        assertThat(card.hasNotDeck("Deck 2")).isTrue();
    }

    @Test
    void testHasNotDeck_id() {
        card.addDeck(deck1);
        assertThat(card.hasNotDeck(1L)).isFalse();
        assertThat(card.hasNotDeck(2L)).isTrue();
    }

    @Test
    void testGetDeckNames() {
        card.addDeck(deck1);
        card.addDeck(deck2);

        var deckNames = card.getDeckNames();
        assertThat(deckNames).containsExactlyInAnyOrder("Deck 1", "Deck 2");
    }


}