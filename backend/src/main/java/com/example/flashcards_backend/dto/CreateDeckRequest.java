package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.annotations.DeckName;
import java.util.Set;

public record CreateDeckRequest(Long subjectId, @DeckName String name, Set<Long> cardIds) {
    public static CreateDeckRequest of(Long subjectId, String name, Long... cardIds) {
        return new CreateDeckRequest(subjectId, name, Set.of(cardIds));
    }
}