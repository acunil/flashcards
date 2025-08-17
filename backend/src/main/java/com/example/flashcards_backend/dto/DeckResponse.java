package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Deck;

public record DeckResponse(Long id, String name) {
    public static DeckResponse fromEntity(Deck deck) {
        return new DeckResponse(
            deck.getId(),
            deck.getName());
    }
}