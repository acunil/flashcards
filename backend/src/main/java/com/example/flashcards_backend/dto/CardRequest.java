package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.annotations.CardContent;
import com.example.flashcards_backend.annotations.DeckName;

import java.util.Set;

public record CardRequest(
    @CardContent String front,
    @CardContent String back,
    Set<@DeckName String> deckNames
) {
    public static CardRequest of(String front, String back) {
        return new CardRequest(front, back, null);
    }

    public static CardRequest of(String front, String back, String... deckNames) {
        return new CardRequest(front, back, Set.of(deckNames));
    }

    public static CardRequest of(String front, String back, Set<String> deckNames) {
        return new CardRequest(front, back, deckNames);
    }
}