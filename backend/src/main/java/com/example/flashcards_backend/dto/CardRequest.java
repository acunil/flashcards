package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.annotations.CardContent;
import com.example.flashcards_backend.annotations.DeckName;
import lombok.Builder;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Builder
public record CardRequest(
        @CardContent String front,
        @CardContent String back,
        @Length(max = 100) String hintFront,
        @Length(max = 100) String hintBack,
        @NonNull
        Long subjectId,
        Set<@DeckName String> deckNames
) {
    public static CardRequest of(String front, String back, Long subjectId) {
        return new CardRequest(front, back, null, null, subjectId, null);
    }

    public static CardRequest of(String front, String back, Long subjectId, String... deckNames) {
        return new CardRequest(front, back, null, null, subjectId, Set.of(deckNames));
    }

}