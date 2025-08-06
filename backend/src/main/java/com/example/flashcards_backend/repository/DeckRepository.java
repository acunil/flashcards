package com.example.flashcards_backend.repository;

import com.example.flashcards_backend.model.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface DeckRepository extends JpaRepository<Deck, Long> {
    @Query("SELECT d FROM Deck d JOIN d.cards c WHERE c.id = :cardId")
    Set<Deck> findDecksByCardId(@Param("cardId") Long cardId);

    Set<Deck> findByNameIn(Set<String> names);
}
