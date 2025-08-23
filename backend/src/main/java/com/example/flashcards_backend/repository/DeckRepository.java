package com.example.flashcards_backend.repository;

import com.example.flashcards_backend.model.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DeckRepository extends JpaRepository<Deck, Long> {
    Set<Deck> findByNameInAndSubjectId(Set<String> names, Long subjectId);

    boolean existsByName(String trim);

    Optional<Deck> findByName(String name);

    @Query("select d from Deck d where d.subject.id = :subjectId")
    List<Deck> findBySubjectId(Long subjectId);
}
