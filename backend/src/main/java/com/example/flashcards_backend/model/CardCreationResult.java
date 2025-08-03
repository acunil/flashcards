package com.example.flashcards_backend.model;

import lombok.NonNull;

public record CardCreationResult(@NonNull Card card, @NonNull Boolean alreadyExisted) {}
