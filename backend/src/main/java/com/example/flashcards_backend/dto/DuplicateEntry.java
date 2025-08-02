package com.example.flashcards_backend.dto;

import lombok.Builder;

@Builder
public record DuplicateEntry(String front, String back) {}