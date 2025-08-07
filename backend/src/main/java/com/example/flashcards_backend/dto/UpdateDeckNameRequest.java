package com.example.flashcards_backend.dto;

import jakarta.validation.constraints.NotEmpty;

public record UpdateDeckNameRequest(@NotEmpty String newName) {
}
