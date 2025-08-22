package com.example.flashcards_backend.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subject")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Getter
@Setter
public class Subject extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "front_label")
    private String frontLabel;

    @Column(name = "back_label")
    private String backLabel;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_side")
    private Side defaultSide;

    @Column(name = "display_deck_names")
    private Boolean displayDeckNames;


    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Deck> decks = new HashSet<>();

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Card> cards = new HashSet<>();

    public enum Side {
        FRONT,
        BACK,
        ANY
    }
}
