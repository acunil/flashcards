package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Card;
import lombok.NonNull;

public record CardCreationResult(@NonNull Card card, @NonNull Boolean alreadyExisted) {}
