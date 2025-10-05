package com.example.flashcards_backend.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CsvUploadResponseDto(List<CardSummary> saved, List<CardSummary> duplicates) {}