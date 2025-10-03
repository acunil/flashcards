package com.example.flashcards_backend.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserDto(String username, UUID id, boolean isActive) {}
