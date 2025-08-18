package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.annotations.CardContent;
import com.example.flashcards_backend.annotations.DeckName;
import lombok.NonNull;

import java.util.Set;

public record CardRequest(
    @CardContent String front,
    @CardContent String back,
    @NonNull
    Long subjectId,
    Set<@DeckName String> deckNames
) {
    public static CardRequest of(String front, String back, Long subjectId) {
        return new CardRequest(front, back, subjectId, null);
    }

    public static CardRequest of(String front, String back, Long subjectId, String... deckNames) {
        return new CardRequest(front, back, subjectId, Set.of(deckNames));
    }

    public static CardRequest of(String front, String back, Long subjectId, Set<String> deckNames) {
        return new CardRequest(front, back, subjectId, deckNames);
    }
}