package com.example.flashcards_backend.repository;

import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardRepositoryCustom {
    List<CardDeckRowProjection> findCardDeckRows(@Param("subjectId") Long subjectId, @Param("cardId") Long cardId);

    List<CardDeckRowProjection> findCardDeckRowsBySubjectId(Long subjectId);

    List<CardDeckRowProjection> findCardDeckRowsByCardId(Long cardId);

    List<CardDeckRowProjection> findAllCardDeckRows();

}
