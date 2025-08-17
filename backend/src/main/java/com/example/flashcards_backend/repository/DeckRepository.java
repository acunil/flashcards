package com.example.flashcards_backend.repository;

import com.example.flashcards_backend.model.Deck;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DeckRepository extends JpaRepository<Deck, Long> {
    Set<Deck> findByNameIn(Set<String> names);

    boolean existsByName(String trim);

    Optional<Deck> findByName(String name);

    @EntityGraph(attributePaths = {"cards"})
    @Query("select d from Deck d")
    List<Deck> findAllWithCards();
}
