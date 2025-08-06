package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Deck;

import java.util.Set;
import java.util.stream.Collectors;

public record DeckResponse(Long id, String name, Set<CardResponse> cardResponses) {
    public static DeckResponse fromEntity(Deck deck) {
        return new DeckResponse(
            deck.getId(),
            deck.getName(),
            deck.getCards().stream()
                .map(CardResponse::fromEntity)
                .collect(Collectors.toSet()));
    }
}