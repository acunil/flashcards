package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.annotations.CardContent;

public record CardRequest(
    @CardContent String front,
    @CardContent String back,
    DeckNamesDto deckNamesDto
) {
    public static CardRequest of(String front, String back) {
        return new CardRequest(front, back, null);
    }

    public static CardRequest of(String front, String back, DeckNamesDto deckNamesDto) {
        return new CardRequest(front, back, deckNamesDto);
    }
}