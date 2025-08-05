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
public class Deck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 40, nullable = false)
    @Size(min = 1, max = 40, message = "Deck name must be between 1 and 40 characters")
    private String name;

    @ManyToMany
    @JoinTable(
        name = "cardDeck",
        joinColumns = @JoinColumn(name = "deckId"),
        inverseJoinColumns = @JoinColumn(name = "cardId")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Card> cards = new HashSet<>();

    public void addCard(Card card) {
        if (card != null) {
            cards.add(card);
            card.getDecks().add(this); // Ensure bidirectional relationship
        }
    }
}