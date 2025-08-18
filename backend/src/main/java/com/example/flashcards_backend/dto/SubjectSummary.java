package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Subject;

public record SubjectSummary(String name, Long id) {
    public static SubjectSummary fromEntity(Subject subject) {
        return new SubjectSummary(subject.getName(), subject.getId());
    }

    public static SubjectSummary of(String name, Long id) {
        return new SubjectSummary(name, id);
    }
}
