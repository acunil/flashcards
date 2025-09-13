package com.example.flashcards_backend.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CreateCardResponse(
    Long id,
    String front,
    String back,
    String hintFront,
    String hintBack,
    List<DeckSummary> decks,
    Boolean alreadyExisted) {}
