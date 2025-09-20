package com.example.flashcards_backend.model;

import lombok.Builder;

import java.util.List;

@Builder
public record CardExport(
    Long cardId,
    String front,
    String back,
    String hintFront,
    String hintBack,
    List<String> deckNames
) {
}
