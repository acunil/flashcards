package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.annotations.DeckName;
import java.util.Set;

public record CreateDeckRequest(@DeckName String name, Set<Long> cardIds) {
    public CreateDeckRequest of(String name, Long... cardIds) {
        return new CreateDeckRequest(name, Set.of(cardIds));
    }
}