package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Deck;

public record DeckSummary(Long id, String name) {
    public static DeckSummary fromEntity(Deck deck) {
        return new DeckSummary(deck.getId(), deck.getName());
    }
}
