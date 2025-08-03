package com.example.flashcards_backend.repository;

import com.example.flashcards_backend.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsByFrontAndBack(String front, String back);

    @Query("SELECT c FROM Card c JOIN CardHistory h ON h.card = c WHERE h.avgRating >= :threshold")
    List<Card> findByMinAvgRating(@Param("threshold") double threshold);

    @Query("SELECT c FROM Card c JOIN CardHistory h ON h.card = c WHERE h.avgRating <= :threshold")
    List<Card> findByMaxAvgRating(@Param("threshold") double threshold);
}