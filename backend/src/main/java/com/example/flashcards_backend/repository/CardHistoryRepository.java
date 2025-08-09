package com.example.flashcards_backend.repository;


import com.example.flashcards_backend.model.CardHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.sql.SQLException;

public interface CardHistoryRepository extends JpaRepository<CardHistory, Long> {

    @Procedure(procedureName = "record_card_rating")
    void recordCardRating(
        @Param("p_card_id") Long cardId,
        @Param("p_rating")  Integer rating
    ) throws SQLException;
}