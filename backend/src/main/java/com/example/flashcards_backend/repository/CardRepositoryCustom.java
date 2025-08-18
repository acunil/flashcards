package com.example.flashcards_backend.repository;

import java.util.Map;

public interface CardRepositoryCustom {
    Map<String, Object> createIfUnique(String front, String back, Long subjectId);
}
