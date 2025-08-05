package com.example.flashcards_backend.dto;

public record AddCardsToDeckRequest(Long deckId, Long[] cardIds) {}
