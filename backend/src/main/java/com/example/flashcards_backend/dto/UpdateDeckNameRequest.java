package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.annotations.DeckName;

public record UpdateDeckNameRequest(@DeckName String newName) {
}
