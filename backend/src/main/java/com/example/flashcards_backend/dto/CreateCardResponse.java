package com.example.flashcards_backend.dto;

import lombok.Builder;

@Builder
public record CreateCardResponse(Long id, String front, String back, Boolean alreadyExisted) {
}