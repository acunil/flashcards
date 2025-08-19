package com.example.flashcards_backend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CardRepositoryImpl implements CardRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<CardDeckRowProjection> findCardDeckRows(Long subjectId, Long cardId) {
        String jpql = """
            SELECT
                c.id AS cardId,
                c.front AS front,
                c.back AS back,
                c.hintFront AS hintFront,
                c.hintBack AS hintBack,
                d.id AS deckId,
                d.name AS deckName,
                ch.avgRating AS avgRating,
                ch.viewCount AS viewCount,
                ch.lastViewed AS lastViewed,
                ch.lastRating AS lastRating,
                s.name AS subjectName,
                s.id AS subjectId
            FROM Card c
            LEFT JOIN c.decks d
            LEFT JOIN c.cardHistories ch
            LEFT JOIN c.subject s
            WHERE (:subjectId IS NULL OR s.id = :subjectId)
            AND (:cardId IS NULL OR c.id = :cardId)
            ORDER BY c.id
        """;
        var query = entityManager.createQuery(jpql, CardDeckRowProjection.class);
        query.setParameter("subjectId", subjectId);
        query.setParameter("cardId", cardId);

        log.info("Attempting to find card deck rows with cardId {} and subjectId {}", cardId, subjectId);
        List<CardDeckRowProjection> resultList = query.getResultList();
        log.info("Found {} card deck rows", resultList.size());
        return resultList;
    }

    @Override
    public List<CardDeckRowProjection> findCardDeckRowsBySubjectId(Long subjectId) {
        return findCardDeckRows(subjectId, null);
    }

    @Override
    public List<CardDeckRowProjection> findCardDeckRowsByCardId(Long cardId) {
        return findCardDeckRows(null, cardId);
    }

    @Override
    public List<CardDeckRowProjection> findAllCardDeckRows() {
        return findCardDeckRows(null, null);
    }

}