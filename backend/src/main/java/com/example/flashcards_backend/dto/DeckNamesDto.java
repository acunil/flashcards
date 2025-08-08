package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Deck;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.stream.Collectors;

public record DeckNamesDto(
    @NotNull
    Set<@NotBlank @Size(min = 1, max = 50) String> deckNames
) {
    public static DeckNamesDto of(Set<Deck> decks) {
        Set<String> deckNames = decks.stream()
            .map(Deck::getName)
            .collect(Collectors.toSet());
        return new DeckNamesDto(deckNames);
    }

    public static DeckNamesDto of(String... deckNames) {
        return new DeckNamesDto(Set.of(deckNames));
    }
}
