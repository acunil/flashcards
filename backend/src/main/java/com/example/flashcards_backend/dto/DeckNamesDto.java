package com.example.flashcards_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record DeckNamesDto(
    @NotNull
    Set<@NotBlank String> deckNames
) {
    public static DeckNamesDto of(Set<String> deckNames) {
        return new DeckNamesDto(deckNames);
    }
}
