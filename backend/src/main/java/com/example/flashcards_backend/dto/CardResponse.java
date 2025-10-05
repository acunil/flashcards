package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Card;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import java.util.stream.Collectors;

public record CardResponse(
    @JsonProperty("id") Long id,
    @JsonProperty("front") String front,
    @JsonProperty("back") String back,
    @JsonProperty("hintFront") String hintFront,
    @JsonProperty("hintBack") String hintBack,
    @JsonProperty("decks") Set<DeckSummary> decks,
    @JsonProperty("subjectId") Long subjectId) {
  public static CardResponse fromEntity(Card card) {
    Set<DeckSummary> deckSummaries = card.getDecks().stream()
        .map(DeckSummary::fromEntity)
        .collect(Collectors.toSet());

    return new CardResponse(
        card.getId(),
        card.getFront(),
        card.getBack(),
        card.getHintFront(),
        card.getHintBack(),
        deckSummaries,
        card.getSubject().getId()
    );
  }
}
