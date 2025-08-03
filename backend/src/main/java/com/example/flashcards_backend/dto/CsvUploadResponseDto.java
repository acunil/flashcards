package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Card;
import lombok.Builder;

import java.util.List;

@Builder
public record CsvUploadResponseDto(List<Card> saved, List<Card> duplicates) {}