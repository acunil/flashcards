package com.example.flashcards_backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    void testBuilder() {
        card = Card.builder()
                .id(1L)
                .subject(subject)
                .front("Front text")
                .back("Back text")
                .subject(Subject.builder().name("Subject 1").id(1L).build())
                .user(User.builder().id(UUID.randomUUID()).build())
                .decks(Set.of(deck1, deck2))
                .sharedWith(Set.of(User.builder().id(UUID.randomUUID()).build()))
                .hintFront("Hint front")
                .hintBack("Hint back")
                .cardHistory(CardHistory.builder().build())
                .build();
        assertThat(card.getCardHistories()).hasSize(1);
        assertThat(card.getSharedWith()).hasSize(1);
        assertThat(card.getDecks()).hasSize(2);
        assertThat(card.getId()).isEqualTo(1L);
        assertThat(card.getFront()).isEqualTo("Front text");
        assertThat(card.getBack()).isEqualTo("Back text");
        assertThat(card.getSubject().getName()).isEqualTo("Subject 1");
        assertThat(card.getUser()).isNotNull();
        assertThat(card.getHintFront()).isEqualTo("Hint front");
        assertThat(card.getHintBack()).isEqualTo("Hint back");
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
    void testAddDecks_wrongSubject() {
        deck1.setSubject(Subject.builder().name("Wrong subject").id(2L).build());
        assertThatThrownBy(() -> card.addDeck(deck1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Deck and Card must belong to the same Subject");
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

    @Test
    void testSharedWith() {
        User user1 = User.builder().id(UUID.randomUUID()).build();
        User user2 = User.builder().id(UUID.randomUUID()).build();
        card.setUser(user1);

        assertThat(card.getSharedWith()).isEmpty();

        card.shareWith(user2);

        assertThat(card.getSharedWith()).containsExactly(user2);
    }
}