package com.example.flashcards_backend.repository;


import com.example.flashcards_backend.model.CardHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public interface CardHistoryRepository extends JpaRepository<CardHistory, Long> {

    @Procedure(procedureName = "record_card_rating")
    void recordCardRating(@Param("p_card_id") Long cardId, @Param("p_rating") Integer rating) throws SQLException;

    @Modifying
    @Query("DELETE FROM CardHistory ch WHERE ch.card.id IN :cardIds")
    void deleteByCardIds(@Param("cardIds") List<Long> cardIds);

    @Query("""
            SELECT COALESCE(SUM(ch.viewCount), 0)
            FROM CardHistory ch
            WHERE ch.user.id = :userId
            """)
    Long totalViewCountByUserId(@Param("userId") UUID userId);

}