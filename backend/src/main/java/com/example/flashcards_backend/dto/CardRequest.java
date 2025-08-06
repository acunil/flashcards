package com.example.flashcards_backend.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record CardRequest(
    @NotBlank String front,
    @NotBlank String back,
    Set<String> decks
) { }