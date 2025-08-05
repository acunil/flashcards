package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Deck;

import java.util.List;

public record DeckResponse(Long id, String name, List<Long> cardIds) {
    public static DeckResponse fromEntity(Deck deck) {
        return new DeckResponse(
            deck.getId(),
            deck.getName(),
            deck.getCards().stream().map(Card::getId).toList()
        );
    }
}