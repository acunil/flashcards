package com.example.flashcards_backend.dto;

public record RemoveCardsFromDeckRequest(Long deckId, Long[] cardIds) {}
