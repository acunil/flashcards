package com.example.flashcards_backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CardRequest(
    @NotBlank @Size(min = 1, max = 200) String front,
    @NotBlank @Size(min = 1, max = 200) String back,
    Set<@NotBlank @Size(min = 1, max = 50) String> decks,
    @Min(1) @Max(5) Integer rating
) {
    public static CardRequest of(String front, String back) {
        return new CardRequest(front, back, null, null);
    }

    public static CardRequest of(String front, String back, int rating) {
        return new CardRequest(front, back, null, rating);
    }
}