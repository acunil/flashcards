package com.example.flashcards_backend.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CreateCardResponse(
        Long id,
        String front,
        String back,
        List<DeckSummary> decks,
        Boolean alreadyExisted
) {
}