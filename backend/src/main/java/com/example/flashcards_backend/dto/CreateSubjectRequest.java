package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Subject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record CreateSubjectRequest(
        @NotEmpty
        String name,
        String frontLabel,
        String backLabel,
        @Valid
        Subject.Side defaultSide,
        Boolean displayDeckNames
) {
    public Subject toEntity() {
        return Subject.builder()
                .name(name)
                .frontLabel(frontLabel)
                .backLabel(backLabel)
                .defaultSide(defaultSide)
                .displayDeckNames(displayDeckNames)
                .build();
    }
}
