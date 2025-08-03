package com.example.flashcards_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record CardRequest(
    @NotBlank String front,
    @NotBlank String back
) { }