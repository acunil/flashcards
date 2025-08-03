package com.example.flashcards_backend.repository;

import com.example.flashcards_backend.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsByFrontAndBack(String front, String back);
}