package com.example.flashcards_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "card", uniqueConstraints = @UniqueConstraint(columnNames = {"front", "back"}))
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 1, max = 100, message = "Front text must be between 1 and 100 characters")
    private String front;

    @Column(nullable = false)
    @Size(min = 1, max = 100, message = "Back text must be between 1 and 100 characters")
    private String back;

    @ManyToMany(mappedBy = "cards")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private Set<Deck> decks = new HashSet<>();

    @OneToMany(mappedBy = "card")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Set<CardHistory> cardHistories = new HashSet<>();

    public void addDeck(Deck deck) {
        decks.add(deck);
        deck.getCards().add(this);
    }

    public void removeDeck(Deck deck) {
        decks.remove(deck);
        deck.getCards().remove(this);
    }

    public void removeAllDecks() {
        for (Deck deck : new HashSet<>(decks)) {
            removeDeck(deck);
        }
    }

    public void addDecks(Set<Deck> newDecks) {
        if (newDecks != null) {
            for (Deck deck : newDecks) {
                addDeck(deck);
            }
        }
    }

    public boolean hasDeck(Deck deck) {
        return decks.contains(deck);
    }

    public boolean hasNotDeck(Deck deck) {
        return !hasDeck(deck);
    }

    public boolean hasDeck(String deckName) {
        return decks.stream().anyMatch(deck -> deck.getName().equals(deckName));
    }

    public boolean hasNotDeck(String deckName) {
        return !hasDeck(deckName);
    }

    public boolean hasDeck(Long deckId) {
        return decks.stream().anyMatch(deck -> deck.getId().equals(deckId));
    }

    public boolean hasNotDeck(Long deckId) {
        return !hasDeck(deckId);
    }

    public Set<String> getDeckNames() {
        Set<String> deckNames = new HashSet<>();
        for (Deck deck : decks) {
            deckNames.add(deck.getName());
        }
        return deckNames;
    }

    public Deck getDeckById(Long deckId) {
        return decks.stream()
                .filter(deck -> deck.getId().equals(deckId))
                .findFirst()
                .orElse(null);
    }

    public void addCardHistory(CardHistory cardHistory) {
        cardHistory.setCard(this);
    }


}