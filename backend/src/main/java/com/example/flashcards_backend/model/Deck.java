package com.example.flashcards_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
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
    @Builder.Default
    private Set<Card> cards = new HashSet<>();

    public void addCard(Card card) {
        if (card != null) {
            cards.add(card);
            card.getDecks().add(this); // Ensure bidirectional relationship
        }
    }

    public void addCards(Set<Card> newCards) {
        if (newCards != null) {
            for (Card card : newCards) {
                addCard(card);
            }
        }
    }

    public void removeCard(Card card) {
        if (card != null && hasCard(card)) {
            cards.remove(card);
            card.getDecks().remove(this); // Ensure bidirectional relationship
        }
    }

    public void removeCards(Set<Card> cardsToRemove) {
        if (cardsToRemove != null) {
            for (Card card : cardsToRemove) {
                removeCard(card);
            }
        }
    }

    @PreRemove
    public void removeAllCards() {
        log.info("Removing all cards from deck with ID: {}", id);
        for (Card card : new HashSet<>(cards)) {
            removeCard(card);
        }
        log.info("Deck was detached from {} cards", cards.size());
    }

    public boolean hasCard(Card card) {
        return cards.contains(card);
    }

    public boolean hasNotCard(Card card) {
        return !hasCard(card);
    }

    public boolean hasNotCard(Long cardId) {
        return cards.stream().noneMatch(card -> card.getId().equals(cardId));
    }

    public Card getCardById(Long cardId) {
        return cards.stream()
            .filter(card -> card.getId().equals(cardId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Card with ID " + cardId + " not found in this deck"));
    }

    public Set<Long> getCardIds() {
        return cards.stream()
            .map(Card::getId)
            .collect(Collectors.toSet());
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Deck deck = (Deck) o;
        return getId() != null && Objects.equals(getId(), deck.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}