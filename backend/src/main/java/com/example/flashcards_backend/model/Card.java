package com.example.flashcards_backend.model;

import com.example.flashcards_backend.annotations.CardContent;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.validator.constraints.Length;

@Slf4j
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "card", uniqueConstraints = @UniqueConstraint(columnNames = {"front", "back"}))
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @CardContent
    private String front;

    @Column(nullable = false)
    @CardContent
    private String back;

    @Column(name = "hint_front")
    @Length(max = 100, message = "Hint can be up to 100 characters")
    private String hintFront;

    @Column(name = "hint_back")
    @Length(max = 100, message = "Hint can be up to 100 characters")
    private String hintBack;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "card_deck",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "deck_id"))
    @Builder.Default
    private Set<Deck> decks = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    @NotNull
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void addDeck(Deck deck) {
        if (!deck.getSubject().equals(this.subject)) {
            throw new IllegalArgumentException("Deck and Card must belong to the same Subject");
        }
        decks.add(deck);
    }

    public void removeDeck(Deck deck) {
        decks.remove(deck);
    }

    public void removeAllDecks() {
        decks.clear();
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof Card other)) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        return getId() != null && Objects.equals(getId(), other.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}