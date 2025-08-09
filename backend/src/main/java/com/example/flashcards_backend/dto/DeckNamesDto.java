package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.annotations.DeckName;
import com.example.flashcards_backend.model.Deck;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.core.util.Json;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public record DeckNamesDto(
    @NotNull
    Set<@DeckName String> deckNames
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

    public JsonNode toJson() {
        return Json.mapper().valueToTree(this);
    }
}
