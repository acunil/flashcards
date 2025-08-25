package com.example.flashcards_backend.repository;

import com.example.flashcards_backend.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, Long> {

    @Modifying
    @Query("DELETE FROM Card c WHERE c.id IN :ids")
    void deleteByIds(List<Long> ids);

    Optional<Card> findBySubjectIdAndFrontAndBack(Long subjectId, String front, String back);

    @Query("""
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
            """)
    List<CardDeckRowProjection> findCardDeckRows(@Param("subjectId") Long subjectId, @Param("cardId") Long cardId);

    default List<CardDeckRowProjection> findCardDeckRowsBySubjectId(Long subjectId) {
        return findCardDeckRows(subjectId, null);
    }

    default List<CardDeckRowProjection> findCardDeckRowsByCardId(Long cardId) {
        return findCardDeckRows(null, cardId);
    }

    @Query("""
            SELECT COUNT(c)
            FROM Card c
            WHERE c.user.id = :userId
            """)
    long countByUserId(@Param("userId") UUID userId);

    Optional<Card> findTopByCardHistories_User_IdOrderByCardHistories_AvgRatingDesc(UUID userId);

    default Optional<Card> findHardestByUserId(UUID userId) {
        return findTopByCardHistories_User_IdOrderByCardHistories_AvgRatingDesc(userId);
    }

    Optional<Card> findTopByCardHistories_User_IdOrderByCardHistories_ViewCountDesc(UUID userId);

    default Optional<Card> findMostViewedByUserId(UUID userId) {
        return findTopByCardHistories_User_IdOrderByCardHistories_ViewCountDesc(userId);
    }

    @Modifying
    @Query(value = "DELETE FROM card_deck WHERE card_id IN :cardIds", nativeQuery = true)
    void deleteDeckAssociationsByCardIds(@Param("cardIds") List<Long> cardIds);

    boolean existsByFrontAndBackAndSubjectId(String front, String back, Long subjectId);

    @Query("""
            SELECT COUNT(c)
            FROM Card c
            LEFT JOIN CardHistory ch ON ch.card = c
            WHERE c.user.id = :userId
              AND (ch IS NULL OR ch.lastViewed IS NULL)
            """)
    Long countByLastViewedIsNullOrZeroAndUserId(@Param("userId") UUID userId);

    default Long countUnviewedByUserId(UUID userId) {
        return countByLastViewedIsNullOrZeroAndUserId(userId);
    }

}