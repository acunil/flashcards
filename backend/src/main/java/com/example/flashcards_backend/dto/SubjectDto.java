package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Subject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Builder
public record SubjectDto(
    Long id,
    @NotEmpty String name,
    String frontLabel,
    String backLabel,
    @Valid Subject.Side defaultSide,
    Boolean displayDeckNames,
    @Valid Subject.CardOrder cardOrder) {
  public static SubjectDto fromEntity(Subject subject) {
    return new SubjectDto(
        subject.getId(),
        subject.getName(),
        subject.getFrontLabel(),
        subject.getBackLabel(),
        subject.getDefaultSide(),
        subject.getDisplayDeckNames(),
        subject.getCardOrder());
  }
}
