package com.example.flashcards_backend.dto;

import java.util.List;

public record CardsDecksResponse(List<CardResponse> cards, List<DeckSummary> deckSummaries) {
}
