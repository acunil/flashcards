package com.example.flashcards_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CardRequest(
    @NotBlank @Size(min = 1, max = 200) String front,
    @NotBlank @Size(min = 1, max = 200) String back,
    Set<String> decks
) {
    public static CardRequest of(String front, String back) {
        return new CardRequest(front, back, null);
    }
}