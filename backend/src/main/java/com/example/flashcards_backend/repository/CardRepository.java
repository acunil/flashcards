package com.example.flashcards_backend.repository;

import com.example.flashcards_backend.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long>, CardRepositoryCustom {
    boolean existsByFrontAndBack(String front, String back);

    @Query("SELECT c FROM Card c JOIN CardHistory h ON h.card = c WHERE h.avgRating >= :threshold")
    List<Card> findByMinAvgRating(@Param("threshold") double threshold);

    @Query("SELECT c FROM Card c JOIN CardHistory h ON h.card = c WHERE h.avgRating <= :threshold")
    List<Card> findByMaxAvgRating(@Param("threshold") double threshold);

    @Query("""
        SELECT
            c.id AS cardId,
            c.front AS front,
            c.back AS back,
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
        ORDER BY c.id
    """)
    List<CardDeckRowProjection> findAllCardDeckRows();

    @Modifying
    @Query("DELETE FROM Card c WHERE c.id IN :ids")
    void deleteCardsById(List<Long> ids);

    @Query("""
        SELECT
            c.id AS cardId,
            c.front AS front,
            c.back AS back,
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
        WHERE s.id = :subjectId
        ORDER BY c.id
    """)
    List<CardDeckRowProjection> findAllCardDeckRowsBySubjectId(Long subjectId);

    Optional<Card> findBySubjectIdAndFrontAndBack(Long subjectId, String front, String back);

}