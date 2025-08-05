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
    private Set<Deck> decks = new HashSet<>();
}