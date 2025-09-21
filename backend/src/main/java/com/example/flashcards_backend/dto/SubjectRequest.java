package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Subject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record SubjectRequest(
    @NotEmpty String name,
    String frontLabel,
    String backLabel,
    @Valid Subject.Side defaultSide,
    Boolean displayDeckNames,
    @Valid Subject.CardOrder cardOrder) {
  public Subject toEntity() {
    return Subject.builder()
        .name(name)
        .frontLabel(frontLabel)
        .backLabel(backLabel)
        .defaultSide(defaultSide)
        .displayDeckNames(displayDeckNames)
        .cardOrder(cardOrder)
        .build();
  }
}
